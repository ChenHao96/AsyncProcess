package org.example.test.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.test.component.BaseProcessTask;
import org.example.test.entity.GradeConfig;
import org.example.test.entity.IntegralRecord;
import org.example.test.entity.PayOrder;
import org.example.test.entity.User;
import org.example.test.entity.enums.IntegralOrderTypeEnum;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.example.test.mapper.GradeConfigMapper;
import org.example.test.mapper.IntegralRecordMapper;
import org.example.test.mapper.PayOrderMapper;
import org.example.test.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Service
public class OrderPayProcessImpl extends BaseProcessTask<Integer> implements OrderPayProcess {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private GradeConfigMapper gradeConfigMapper;

    @Autowired
    private IntegralRecordMapper integralRecordMapper;

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @PostConstruct
    public void initMethod() {
        int proCoreCount = Math.max(Runtime.getRuntime().availableProcessors(), 12);
        super.initMethod(proCoreCount);
    }

    @PreDestroy
    public void destroyMethod() {
        super.destroyMethod();
    }

    @Override
    public void abstractProcessMethod(Integer orderId) {
        if (orderId == null || orderId < 1) return;
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", orderId);
        PayOrder process = payOrderMapper.selectOne(queryWrapper);
        if (PayOrderStatusEnum.PAID.equals(process.getStatus())) {
            processSuccess(process);
        } else if (PayOrderStatusEnum.CREATE.equals(process.getStatus())) {
            processFail(process);
        }
    }

    @Override
    public void addPendingTaskBean(Integer orderId) {
        if (orderId == null || orderId < 1) return;
        super.addPendingTaskBean(orderId);
    }

    public void processFail(PayOrder process) {

        if (process == null || !PayOrderStatusEnum.CREATE.equals(process.getStatus())) return;
        PlatformTransactionManager manager = transactionTemplate.getTransactionManager();

        if (manager != null) {
            int updateCount = 0;
            final int databaseUpdateRow = 2;

            TransactionStatus status = getTransactionStatus(manager);
            try {
                //1.取消库存
                updateCount += productService.cancelStock(process.getProductId(), process.getProductCount());
                //2.订单修改为失败
                process.setStatus(PayOrderStatusEnum.FAILED);
                updateCount += payOrderMapper.updateById(process);
            } catch (Exception e) {
                log.info("processFail exception......");
                manager.rollback(status);
                return;
            }

            if (updateCount != databaseUpdateRow) {
                log.info("processFail fail...... orderId:{},updateCount:{}", process.getId(), updateCount);
                manager.rollback(status);
                super.addPendingTaskBean(process.getId());
            } else {
                manager.commit(status);
            }
        }
    }

    public void processSuccess(PayOrder process) {
        if (process == null || !PayOrderStatusEnum.PAID.equals(process.getStatus())) return;
        User user = userMapper.selectById(process.getUserId());
        if (user == null) return;
        user.setOrderCount(user.getOrderCount() + 1);
        user.setIntegral(user.getIntegral() + process.getIntegral());

        PlatformTransactionManager manager = transactionTemplate.getTransactionManager();
        if (manager != null) {
            int updateCount = 0;
            final int databaseUpdateRow = 4;
            TransactionStatus status = getTransactionStatus(manager);
            try {
                //1.修改用户积分和等级
                updateCount += updateUserGrade(user);
                //2.添加积分记录
                updateCount += addIntegralRecord(process, user);
                //3.修改订单状态
                process.setStatus(PayOrderStatusEnum.PENDING);
                updateCount += payOrderMapper.updateById(process);
                //4.将商品库存提交
                updateCount += productService.commitStock(process.getProductId(), process.getProductCount());
            } catch (Exception e) {
                log.info("processSuccess exception......");
                manager.rollback(status);
                return;
            }

            if (updateCount != databaseUpdateRow) {
                log.info("processSuccess fail...... orderId:{},updateCount:{}", process.getId(), updateCount);
                manager.rollback(status);
                super.addPendingTaskBean(process.getId());
            } else {
                manager.commit(status);
            }
        }
    }

    private TransactionStatus getTransactionStatus(PlatformTransactionManager manager) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(Isolation.READ_UNCOMMITTED.value());
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return manager.getTransaction(def);
    }

    private int addIntegralRecord(PayOrder process, User user) {
        IntegralRecord integralRecord = new IntegralRecord();
        integralRecord.setUserId(user.getId());
        integralRecord.setOrderId(process.getId());
        integralRecord.setHistory(user.getIntegral());
        integralRecord.setIntegral(process.getIntegral());
        integralRecord.setOrderType(IntegralOrderTypeEnum.PRODUCT);
        return integralRecordMapper.insert(integralRecord);
    }

    private int updateUserGrade(User user) {
        QueryWrapper<GradeConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("integral", user.getIntegral());
        queryWrapper.gt("grade", user.getGrade());
        queryWrapper.orderByDesc("grade");
        queryWrapper.last("limit 1");
        GradeConfig config = gradeConfigMapper.selectOne(queryWrapper);
        if (config != null) {
            if (user.getGrade() < config.getGrade()) {
                user.setGrade(config.getGrade());
            }
        }
        return userMapper.updateById(user);
    }
}

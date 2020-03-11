package org.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.test.component.OrderPayFinishProcess;
import org.example.test.entity.GradeConfig;
import org.example.test.entity.IntegralRecord;
import org.example.test.entity.PayOrder;
import org.example.test.entity.User;
import org.example.test.entity.enums.IntegralOrderTypeEnum;
import org.example.test.mapper.GradeConfigMapper;
import org.example.test.mapper.IntegralRecordMapper;
import org.example.test.mapper.UserMapper;
import org.example.test.service.OrderPayFailProcess;
import org.example.test.service.OrderPaySuccessProcess;
import org.example.test.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
public class OrderPayProcessImpl implements OrderPayFailProcess, OrderPaySuccessProcess {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private GradeConfigMapper gradeConfigMapper;

    @Autowired
    private IntegralRecordMapper integralRecordMapper;

    @Autowired
    private OrderPayFinishProcess orderPayFinishProcess;

    @Override
    @Transactional
    public void processFail(PayOrder payOrder) {
        if (payOrder == null) return;
        //取消库存
        int updateCount = productService.cancelStock(payOrder.getProductId(), payOrder.getProductCount());
        if (updateCount != 1) {
            log.warn("processFail: order:{}", payOrder);
            orderPayFinishProcess.addPendingOrder(payOrder);
        }
    }

    @Override
    @Transactional
    public void processSuccess(PayOrder payOrder) {

        if (payOrder == null) return;
        User user = userMapper.selectById(payOrder.getUserId());
        if (user == null) return;
        user.setIntegral(user.getIntegral() + payOrder.getIntegral());

        QueryWrapper<GradeConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("integral", user.getIntegral());
        queryWrapper.gt("grade", user.getGrade());
        queryWrapper.orderByDesc("grade");
        queryWrapper.last("limit 1");
        GradeConfig config = gradeConfigMapper.selectOne(queryWrapper);
        if (config == null) return;

        if (user.getGrade() < config.getGrade()) {
            user.setGrade(config.getGrade());
        }

        IntegralRecord integralRecord = new IntegralRecord();
        integralRecord.setUserId(user.getId());
        integralRecord.setOrderId(payOrder.getId());
        integralRecord.setHistory(user.getIntegral());
        integralRecord.setIntegral(payOrder.getIntegral());
        integralRecord.setOrderType(IntegralOrderTypeEnum.PRODUCT);

        final int databaseUpdateRow = 3;
        int updateCount = userMapper.updateById(user);
        updateCount += productService.commitStock(payOrder.getProductId(), payOrder.getProductCount());
        updateCount += integralRecordMapper.insert(integralRecord);
        if (updateCount != databaseUpdateRow) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.warn("processSuccess: order:{}", payOrder);
            orderPayFinishProcess.addPendingOrder(payOrder);
        }
    }
}

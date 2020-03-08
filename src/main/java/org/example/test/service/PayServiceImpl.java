package org.example.test.service;

import org.example.test.entity.PayOrder;
import org.example.test.entity.User;
import org.example.test.entity.enums.IntegralOrderTypeEnum;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private GradeConfigService gradeConfigService;

    @Autowired
    private IntegralRecordService integralRecordService;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public PayOrderStatusEnum processPayOrder(String orderNumber) {
        PayOrderStatusEnum result = PayOrderStatusEnum.FAILED;
        PayOrder payOrder = payOrderService.queryPayOrderByNumber(orderNumber);
        if (payOrder != null) {
            if (PayOrderStatusEnum.CREATE.equals(payOrder.getStatus())) {
                int updateCount = 0;
                final int databaseUpdateRow;
                if (walletService.payOrderDeduction(payOrder.getUserId(), payOrder.getTotalPrice())) {//扣款成功
                    databaseUpdateRow = 4;
                    //1.1 修改订单为已支付
                    payOrder.setStatus(PayOrderStatusEnum.PAID);
                    //1.2 将库存提交
                    updateCount += productService.commitStock2(payOrder.getProductId(), payOrder.getProductCount());

                    User user = userService.queryUserById(payOrder.getUserId());
                    //1.3 添加积分记录
                    updateCount += integralRecordService.addIntegralRecord2(user, payOrder.getId(),
                            payOrder.getIntegral(), IntegralOrderTypeEnum.PRODUCT);
                    //1.4 修改用户消费积分和等级
                    gradeConfigService.updateUserGrade2(user, payOrder.getIntegral());
                    user.setIntegral(user.getIntegral() + payOrder.getIntegral());
                    updateCount += userService.paySuccessUpdateUser(user);
                } else {//扣款失败
                    databaseUpdateRow = 1;
                    //2.1 修改订单为失败
                    payOrder.setStatus(PayOrderStatusEnum.FAILED);
                }
                updateCount += payOrderService.updatePayOrder2(payOrder);
                if (updateCount != databaseUpdateRow)
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            result = payOrder.getStatus();
        }
        return result;
    }
}

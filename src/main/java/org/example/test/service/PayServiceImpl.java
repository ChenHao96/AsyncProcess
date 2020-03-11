package org.example.test.service;

import org.example.test.component.OrderPayFinishProcess;
import org.example.test.entity.PayOrder;
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
    private WalletService walletService;

    @Autowired
    private OrderPayFinishProcess orderPayFinishProcess;

    @Override
    @Transactional
    public PayOrderStatusEnum processPayOrder(String orderNumber) {
        PayOrderStatusEnum result = PayOrderStatusEnum.FAILED;
        PayOrder payOrder = payOrderService.queryPayOrderByNumber(orderNumber);
        if (payOrder != null) {
            if (PayOrderStatusEnum.CREATE.equals(payOrder.getStatus())) {
                final int databaseUpdateRow;
                Integer walletId = walletService.tryDeduction(payOrder.getTotalPrice(), payOrder.getUserId());
                int updateCount = 0;
                if (walletId != null && walletId > 0) {//扣款成功
                    databaseUpdateRow = 2;
                    //1.扣款锁定
                    updateCount += 1;
                    payOrder.setWalletId(walletId);
                    //2.修改订单已支付
                    payOrder.setStatus(PayOrderStatusEnum.PAID);
                } else {//扣款失败
                    databaseUpdateRow = 1;
                    //1.修改订单失败
                    payOrder.setStatus(PayOrderStatusEnum.FAILED);
                }
                updateCount += payOrderService.updatePayOrder(payOrder);
                if (updateCount != databaseUpdateRow) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                } else {
                    orderPayFinishProcess.addPendingOrder(payOrder);
                }
            }
            result = payOrder.getStatus();
        }
        return result;
    }
}

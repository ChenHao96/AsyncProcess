package org.example.test.service;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.PayOrder;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Service
public class AsyncPayServiceImpl implements AsyncPayService {

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private SuccessOrderProcess successOrderProcess;

    @Override
    @Transactional
    public PayOrderStatusEnum processPayOrder(PayOrder payOrder) {
        PayOrderStatusEnum result = PayOrderStatusEnum.FAILED;
        if (payOrder != null) {
            boolean success = false;
            Future<PayOrderStatusEnum> orderFuture = payOrderService.updatePayOrder(payOrder);
            Future<Boolean> deductionFuture = walletService.payOrderDeduction(payOrder.getTotalPrice(), payOrder.getUserId());

            try {
                result = orderFuture.get();
                if (PayOrderStatusEnum.CREATE.equals(result)) {
                    if (deductionFuture.get()) {
                        success = true;
                        result = PayOrderStatusEnum.PAID;
                        successOrderProcess.addSuccessPayOrder(payOrder);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                log.warn("processPayOrderNumber:{}", e.getMessage());
            }

            if (!success) {
                if (PayOrderStatusEnum.FAILED.equals(result)) {
                    successOrderProcess.addFailPayOrder(payOrder);
                }
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return result;
    }
}

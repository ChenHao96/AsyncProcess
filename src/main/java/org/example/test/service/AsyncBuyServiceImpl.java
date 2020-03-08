package org.example.test.service;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.PayOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Service
public class AsyncBuyServiceImpl implements AsyncBuyService {

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public PayOrder panicBuying(Integer userId, Integer productId, Integer productCount) {
        boolean success = false;
        PayOrder order = null;
        //1.锁定商品库存
        Future<Boolean> updateProductFuture = productService.updateProductTryStock(productId, productCount);
        //2.添加商品订单
        Future<PayOrder> addPayOrder = payOrderService.addPayOrderRecord(userId, productId, productCount);
        try {
            if (updateProductFuture.get()) {
                order = addPayOrder.get();
                if (order != null) {
                    success = true;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("panicBuying:{}", e.getMessage());
        }
        if (!success) TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return order;
    }
}

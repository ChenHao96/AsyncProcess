package org.example.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.UUID;

@Service
public class BuyServiceImpl implements BuyService {

    @Autowired
    private PayOrderService payOrderService;

    @Override
    @Transactional
    public String panicBuying(Integer userId, Integer productId, Integer productCount) {
        String orderNumber = UUID.randomUUID().toString();
        //1.添加商品订单
        if (!payOrderService.addPayOrderRecord(userId, productId, productCount, orderNumber)) {
            orderNumber = null;
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return orderNumber;
    }
}

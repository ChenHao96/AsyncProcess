package org.example.test.service;

import org.example.test.entity.PayOrder;
import org.example.test.entity.enums.PayOrderStatusEnum;

import java.util.concurrent.Future;

public interface PayOrderService {

    Future<PayOrder> addPayOrderRecord(Integer userId, Integer productId, Integer productCount);

    boolean addPayOrderRecord(Integer userId, Integer productId, Integer productCount, String orderNumber);

    PayOrder queryPayOrderByNumber(String orderNumber);

    Future<PayOrderStatusEnum> updatePayOrder(PayOrder payOrder);

    int updatePayOrder2(PayOrder payOrder);
}

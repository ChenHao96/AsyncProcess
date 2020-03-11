package org.example.test.service;

import org.example.test.entity.PayOrder;

public interface PayOrderService {

    PayOrder queryPayOrderByNumber(String orderNumber);

    int updatePayOrder(PayOrder payOrder);

    int addPayOrderRecord(Integer userId, Integer productId, Integer productCount, String orderNumber);
}

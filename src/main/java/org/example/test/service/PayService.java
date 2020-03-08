package org.example.test.service;

import org.example.test.entity.enums.PayOrderStatusEnum;

public interface PayService {

    PayOrderStatusEnum processPayOrderNumber(String orderNumber);
}

package org.example.test.service;

import org.example.test.entity.PayOrder;
import org.example.test.entity.enums.PayOrderStatusEnum;

public interface AsyncPayService {

    PayOrderStatusEnum processPayOrder(PayOrder payOrder);
}

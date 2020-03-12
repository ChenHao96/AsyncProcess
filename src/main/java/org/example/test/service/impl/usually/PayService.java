package org.example.test.service.impl.usually;

import org.example.test.entity.enums.PayOrderStatusEnum;

public interface PayService {

    PayOrderStatusEnum processPayOrder(String orderNumber);
}

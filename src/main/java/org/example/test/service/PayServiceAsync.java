package org.example.test.service;

import org.example.test.entity.PayOrder;
import org.example.test.entity.enums.PayOrderStatusEnum;

public interface PayServiceAsync {

    PayOrderStatusEnum processPayOrderAsync(PayOrder payOrder);
}

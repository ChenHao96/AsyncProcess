package org.example.test.service;

import org.example.test.entity.PayOrder;

public interface AsyncBuyService {

    PayOrder panicBuying(Integer userId, Integer productId, Integer productCount);
}

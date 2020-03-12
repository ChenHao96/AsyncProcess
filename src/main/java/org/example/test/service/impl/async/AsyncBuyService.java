package org.example.test.service.impl.async;

import org.example.test.entity.PayOrder;

public interface AsyncBuyService {

    PayOrder panicBuying(Integer userId, Integer productId, Integer productCount);
}

package org.example.test;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.PayOrder;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.example.test.model.OrderParam;
import org.example.test.service.AsyncBuyService;
import org.example.test.service.AsyncPayService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class AsyncTest extends TestData {

    @Autowired
    private AsyncBuyService buyService;

    @Autowired
    private AsyncPayService payService;

    @Test
    public void test() {
        for (OrderParam param : testData) {
            long startTime = System.currentTimeMillis();
            PayOrder order = buyService.panicBuying(param.getUserId(), param.getProductId(), param.getProductCount());
            log.info("{} panicBuying:{}, use time:{}", param.getIndex(), order, System.currentTimeMillis() - startTime);
            if (order == null) {
                log.info("{} 订单创建失败, userId;{} productId:{}", param.getIndex(), param.getUserId(), param.getProductId());
                continue;
            }
            startTime = System.currentTimeMillis();
            PayOrderStatusEnum statusEnum = payService.processPayOrder(order);
            log.info("{} processPayOrderNumber:{}, use time:{}", param.getIndex(), statusEnum, System.currentTimeMillis() - startTime);
            if (!PayOrderStatusEnum.PAID.equals(statusEnum)) {
                log.info("{} 订单支付失败, userId;{} productId:{}", param.getIndex(), param.getUserId(), param.getProductId());
            }
        }
    }
}

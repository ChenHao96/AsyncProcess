package org.example.test;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.example.test.model.OrderParam;
import org.example.test.service.BuyService;
import org.example.test.service.PayService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@Slf4j
public class UsuallyTest extends TestData {

    @Autowired
    private BuyService buyService;

    @Autowired
    private PayService payService;

    @Test
    public void test() {
        for (OrderParam param : testData) {
            long startTime = System.currentTimeMillis();
            String orderNumber = buyService.panicBuying(param.getUserId(), param.getProductId(), param.getProductCount());
            log.info("{} panicBuying:{}, use time:{}", param.getIndex(), orderNumber, System.currentTimeMillis() - startTime);
            if (StringUtils.isEmpty(orderNumber)) {
                log.info("{} 订单创建失败, userId;{} productId:{}", param.getIndex(), param.getUserId(), param.getProductId());
                continue;
            }
            startTime = System.currentTimeMillis();
            PayOrderStatusEnum statusEnum = payService.processPayOrder(orderNumber);
            log.info("{} processPayOrderNumber:{}, use time:{}", param.getIndex(), statusEnum, System.currentTimeMillis() - startTime);
            if (!PayOrderStatusEnum.PAID.equals(statusEnum)) {
                log.info("{} 订单支付失败, userId;{} productId:{}", param.getIndex(), param.getUserId(), param.getProductId());
            }
        }
    }
}

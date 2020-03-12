package org.example.test;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.example.test.model.OrderParam;
import org.example.test.service.BuyService;
import org.example.test.service.PayService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class UsuallyTest extends TestData {

    @Autowired
    private BuyService buyService;

    @Autowired
    private PayService payService;

    @Test
    public void test() throws InterruptedException {

        final AtomicInteger dataCount = new AtomicInteger();
        final AtomicInteger pollOrderNumber = new AtomicInteger();
        final AtomicInteger panicBuyingResponse = new AtomicInteger();

        CountDownLatch latch = new CountDownLatch(testData.size() * 2);
        ExecutorService executorService = Executors.newFixedThreadPool(testData.size() * 2);
        for (final List<OrderParam> data : testData) {

            executorService.submit(() -> {
                try {
                    for (final OrderParam param : data) {
                        dataCount.incrementAndGet();
                        if (param.getProductCount() == 0) continue;
                        String orderNumber = buyService.panicBuying(param.getUserId(), param.getProductId(), param.getProductCount());
                        panicBuyingResponse.incrementAndGet();
                        if (!StringUtils.isEmpty(orderNumber)) {
                            buySuccess.incrementAndGet();
                            futures.add(orderNumber);
                        }
                    }
                } catch (Exception e) {
                    log.warn("panicBuying:{}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });

            executorService.submit(() -> {
                try {
                    while (true) {
                        Object order = futures.poll(3, TimeUnit.SECONDS);
                        if (order == null) return;
                        String orderNumber = (String) order;
                        if (!StringUtils.isEmpty(orderNumber)) {
                            pollOrderNumber.incrementAndGet();
                            PayOrderStatusEnum statusEnum = payService.processPayOrder(orderNumber);
                            if (PayOrderStatusEnum.PAID.equals(statusEnum)) {
                                paySuccess.incrementAndGet();
                            } else {
                                payFail.incrementAndGet();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    log.warn("processPayOrder:{}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        log.info("dataCount:{},panicBuyingResponse:{},pollOrderNumber:{}", dataCount.get(), panicBuyingResponse.get(), pollOrderNumber.get());
    }
}

package org.example.test;

import lombok.extern.slf4j.Slf4j;
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
    public void test() throws InterruptedException {
        //TODO:bug多个请求下直接卡死，需要改进
//        final CountDownLatch latch = new CountDownLatch(2);
//        new Thread(() -> {
//            try {
//                for (final OrderParam param : testData) {
//                    futures.add(executorService.submit(() -> buyService.panicBuying(param.getUserId(), param.getProductId(), param.getProductCount())));
//                }
//            } finally {
//                latch.countDown();
//            }
//        }).start();
//
//        new Thread(() -> {
//            try {
//                while (true) {
//                    Future<Object> future = futures.poll(3, TimeUnit.SECONDS);
//                    if (future == null) return;
//                    PayOrder payOrder = (PayOrder) future.get();
//                    if (payOrder != null) {
//                        PayOrderStatusEnum statusEnum = payService.processPayOrder(payOrder);
//                        log.info("process status:{}", statusEnum);
//                    }
//                }
//            } catch (InterruptedException | ExecutionException e) {
//                log.warn("processPayOrder:{}", e.getMessage());
//            } finally {
//                latch.countDown();
//            }
//        }).start();
//
//        latch.await();
    }
}

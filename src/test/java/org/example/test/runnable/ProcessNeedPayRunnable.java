package org.example.test.runnable;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.example.test.service.PayService;
import org.springframework.util.StringUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Slf4j
@Accessors(chain = true)
public class ProcessNeedPayRunnable implements Runnable {

    private CountDownLatch latch;
    private AtomicInteger payFail;
    private PayService payService;
    private AtomicInteger paySuccess;
    private BlockingQueue<Future<Object>> futures;

    @Override
    public void run() {
        try {
            while (true) {
                Future<Object> future = futures.poll(3, TimeUnit.SECONDS);
                if (future == null) return;
                String orderNumber = (String) future.get();
                if (!StringUtils.isEmpty(orderNumber)) {
                    PayOrderStatusEnum statusEnum = payService.processPayOrder(orderNumber);
                    if (PayOrderStatusEnum.PAID.equals(statusEnum)) {
                        paySuccess.incrementAndGet();
                    } else if (PayOrderStatusEnum.FAILED.equals(statusEnum)) {
                        payFail.incrementAndGet();
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.warn("processPayOrder:{}", e.getMessage());
        } finally {
            latch.countDown();
        }
    }
}

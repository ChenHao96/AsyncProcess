package org.example.test.runnable;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.example.test.model.OrderParam;
import org.example.test.service.BuyService;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Slf4j
@Accessors(chain = true)
public class ProcessBuyRunnable implements Runnable {

    private CountDownLatch latch;
    private BuyService buyService;
    private AtomicInteger buySuccess;
    private List<OrderParam> testData;
    private ExecutorService executorService;
    private BlockingQueue<Future<Object>> futures;

    @Override
    public void run() {
        try {
            for (final OrderParam param : testData) {
                futures.add(executorService.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        String orderNumber = buyService.panicBuying(param.getUserId(), param.getProductId(), param.getProductCount());
                        if (!StringUtils.isEmpty(orderNumber)) {
                            buySuccess.incrementAndGet();
                        }
                        return orderNumber;
                    }
                }));
            }
        } finally {
            latch.countDown();
        }
    }
}

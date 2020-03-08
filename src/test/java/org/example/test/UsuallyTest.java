package org.example.test;

import lombok.extern.slf4j.Slf4j;
import org.example.test.model.OrderParam;
import org.example.test.runnable.ProcessBuyRunnable;
import org.example.test.runnable.ProcessNeedPayRunnable;
import org.example.test.service.BuyService;
import org.example.test.service.PayService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class UsuallyTest extends TestData {

    @Autowired
    private BuyService buyService;

    @Autowired
    private PayService payService;

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(testData.size() * 2);
        for (final List<OrderParam> data : testData) {
            ProcessBuyRunnable processBuyRunnable = new ProcessBuyRunnable();
            processBuyRunnable.setLatch(latch);
            processBuyRunnable.setTestData(data);
            processBuyRunnable.setFutures(futures);
            processBuyRunnable.setBuyService(buyService);
            processBuyRunnable.setBuySuccess(buySuccess);
            processBuyRunnable.setExecutorService(executorService);
            new Thread(processBuyRunnable).start();

            ProcessNeedPayRunnable processNeedPayRunnable = new ProcessNeedPayRunnable();
            processNeedPayRunnable.setLatch(latch);
            processNeedPayRunnable.setFutures(futures);
            processNeedPayRunnable.setPayFail(payFail);
            processNeedPayRunnable.setPayService(payService);
            processNeedPayRunnable.setPaySuccess(paySuccess);
            new Thread(processNeedPayRunnable).start();
        }
        latch.await();
    }
}

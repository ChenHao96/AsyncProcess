package org.example.test.component;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.PayOrder;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.example.test.service.OrderPayFailProcess;
import org.example.test.service.OrderPaySuccessProcess;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

@Slf4j
@Component
public class OrderProcessComponent implements OrderPayFinishProcess, Runnable {

    private int proCoreCount;
    private boolean runnable;
    private ExecutorService executorService;
    private final BlockingQueue<PayOrder> blockingQueue;

    @Autowired
    private OrderPayFailProcess failProcess;

    @Autowired
    private OrderPaySuccessProcess successProcess;

    public OrderProcessComponent() {
        this.runnable = false;
        this.blockingQueue = new LinkedBlockingQueue<>();
        this.proCoreCount = Math.max(Runtime.getRuntime().availableProcessors(), 12) * 2;
    }

    @PostConstruct
    public void initMethod() {
        this.runnable = true;
        this.executorService = Executors.newFixedThreadPool(this.proCoreCount);
        log.info("OrderProcess ExecutorService init success.");
//        for (int i = 0; i < this.proCoreCount; i++) {
//            this.executorService.submit(this);
//        }
        log.info("OrderProcess ExecutorService process task:{}.", this.proCoreCount);
    }

    @PreDestroy
    public void destroyMethod() {
        this.runnable = false;
        executorService.shutdown();
        log.info("OrderProcess ExecutorService shutdown...");
        try {
            if (!executorService.awaitTermination(3, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
                    log.error("OrderProcess ExecutorService Pool did not terminate.");
                }
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            log.warn("OrderProcess ExecutorService shutdown fail.");
        }
        log.info("OrderProcess ExecutorService shutdown success.");
    }

    @Override
    public void addPendingOrder(PayOrder source) {
        if (source == null || !this.runnable) return;
        PayOrder process = new PayOrder();
        BeanUtils.copyProperties(source, process);
        this.blockingQueue.add(process);
    }

    @Override
    public void run() {
        while (true) {
            PayOrder payOrder;
            try {
                payOrder = this.blockingQueue.poll(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.warn("", e);
                return;
            }
            if (payOrder == null) {
                if (!this.runnable) break;
                continue;
            }
            if (PayOrderStatusEnum.PAID.equals(payOrder.getStatus())) {
                successProcess.processSuccess(payOrder);
            } else if (PayOrderStatusEnum.FAILED.equals(payOrder.getStatus())) {
                failProcess.processFail(payOrder);
            }
        }
    }
}

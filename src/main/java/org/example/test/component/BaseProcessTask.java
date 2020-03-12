package org.example.test.component;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public abstract class BaseProcessTask<D> implements Runnable {

    private boolean runnable;
    private CountDownLatch latch;
    private BlockingQueue<D> blockingQueue;

    protected void initMethod(int proCoreCount) {
        this.runnable = true;
        this.latch = new CountDownLatch(proCoreCount);
        this.blockingQueue = new LinkedBlockingQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(proCoreCount);
        log.info("ExecutorService init success.");
        for (int i = 0; i < proCoreCount; i++) {
            executorService.execute(this);
        }
        log.info("ExecutorService process task:{}.", proCoreCount);
    }

    protected void destroyMethod() {
        this.runnable = false;
        log.info("ExecutorService shutdown...");
        try {
            this.latch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.warn("ExecutorService Pool did not terminate", e);
        }
        log.info("ExecutorService shutdown success.");
    }

    public void addPendingTaskBean(D bean) {
        if (bean == null || this.blockingQueue == null || !this.runnable) return;
        this.blockingQueue.add(bean);
    }

    @Override
    public void run() {
        try {
            while (true) {
                D data;
                try {
                    data = this.blockingQueue.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.warn("", e);
                    return;
                }
                if (data == null) {
                    if (!this.runnable) break;
                    continue;
                }
                abstractProcessMethod(data);
            }
        } finally {
            this.latch.countDown();
        }
    }

    protected abstract void abstractProcessMethod(D data);
}

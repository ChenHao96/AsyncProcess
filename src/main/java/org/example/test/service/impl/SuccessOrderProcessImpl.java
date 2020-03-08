package org.example.test.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.PayOrder;
import org.example.test.service.SuccessOrderProcess;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SuccessOrderProcessImpl implements SuccessOrderProcess {

    @Override
    public void addSuccessPayOrder(PayOrder process) {
        //TODO:
        log.info("addSuccessPayOrder:{}", process);
    }

    @Override
    public void addFailPayOrder(PayOrder payOrder) {
        //TODO:
        log.info("addFailPayOrder:{}", payOrder);
    }
}

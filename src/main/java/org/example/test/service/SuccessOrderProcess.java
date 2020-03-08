package org.example.test.service;

import org.example.test.entity.PayOrder;

public interface SuccessOrderProcess {

    void addSuccessPayOrder(PayOrder process);

    void addFailPayOrder(PayOrder payOrder);
}

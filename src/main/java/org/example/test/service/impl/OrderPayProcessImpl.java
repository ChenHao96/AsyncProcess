package org.example.test.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.PayOrder;
import org.example.test.mapper.IntegralRecordMapper;
import org.example.test.mapper.UserMapper;
import org.example.test.service.OrderPayFailProcess;
import org.example.test.service.OrderPaySuccessProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderPayProcessImpl implements OrderPayFailProcess, OrderPaySuccessProcess {

    @Autowired
    private IntegralRecordMapper integralRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void processFail(PayOrder payOrder) {
        //TODO:
    }

    @Override
    public void processSuccess(PayOrder payOrder) {
        //TODO:
    }
}

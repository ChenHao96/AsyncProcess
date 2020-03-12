package org.example.test.service.impl.async;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.PayOrder;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AsyncPayServiceImpl implements AsyncPayService {

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public PayOrderStatusEnum processPayOrder(PayOrder payOrder) {
        //TODO:
        return null;
    }
}

package org.example.test.service.impl.async;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.PayOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AsyncBuyServiceImpl implements AsyncBuyService {

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public PayOrder panicBuying(Integer userId, Integer productId, Integer productCount) {
        //TODO:
        return null;
    }
}

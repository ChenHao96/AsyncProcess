package org.example.test;

import lombok.extern.slf4j.Slf4j;
import org.example.test.entity.Product;
import org.example.test.entity.User;
import org.example.test.mapper.ProductMapper;
import org.example.test.mapper.UserMapper;
import org.example.test.model.OrderParam;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class TestData extends BaseTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    protected List<List<OrderParam>> testData;

    protected BlockingQueue<Object> futures;

    protected final AtomicInteger buySuccess = new AtomicInteger();
    protected final AtomicInteger paySuccess = new AtomicInteger();
    protected final AtomicInteger payFail = new AtomicInteger();

    @Before
    public void testBefore() {
        final int queueCount = 10, taskCount = 100;
        Random random = new Random();
        List<User> users = userMapper.selectList(null);
        List<Product> products = productMapper.selectList(null);
        testData = new ArrayList<>(queueCount);
        for (int i = 0; i < queueCount; i++) {
            List<OrderParam> list = new ArrayList<>(taskCount);
            for (int y = 0; y < taskCount; y++) {
                Product product = products.get(random.nextInt(products.size()));
                if (product.getSurplusStock() <= 0) continue;
                OrderParam param = new OrderParam();
                param.setProductId(product.getId());
                param.setProductCount(random.nextInt(5));
                param.setUserId(users.get(random.nextInt(users.size())).getId());
                list.add(param);
            }
            testData.add(list);
        }
        futures = new LinkedBlockingQueue<>(queueCount * taskCount);
    }

    @After
    public void testAfter() {
        log.info("test result: orderCount:{}, paySuccess:{}, payFail:{}", buySuccess, paySuccess, payFail);
    }
}

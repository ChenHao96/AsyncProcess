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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class TestData extends BaseTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    protected List<List<OrderParam>> testData;

    protected ExecutorService executorService;

    protected BlockingQueue<Future<Object>> futures;

    protected final AtomicInteger buySuccess = new AtomicInteger();
    protected final AtomicInteger paySuccess = new AtomicInteger();
    protected final AtomicInteger payFail = new AtomicInteger();

    @Before
    public void testBefore() {
        final int count = 10;
        final int item = 100;
        Random random = new Random();
        List<User> users = userMapper.selectList(null);
        List<Product> products = productMapper.selectList(null);
        testData = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            List<OrderParam> list = new ArrayList<>(item);
            for (int y = 0; y < item; y++) {
                Product product = products.get(random.nextInt(products.size()));
                if (product.getSurplusStock() <= 0) continue;
                OrderParam param = new OrderParam();
                param.setProductId(product.getId());
                param.setUserId(users.get(random.nextInt(users.size())).getId());
                param.setProductCount(random.nextInt(product.getSurplusStock()));
                list.add(param);
            }
            testData.add(list);
        }
        futures = new LinkedBlockingQueue<>(count * item);
        executorService = Executors.newFixedThreadPool(100);
    }

    @After
    public void testAfter() {
        log.info("test result: buy:{}, pay:{}, fail:{}", buySuccess, paySuccess, payFail);
    }
}

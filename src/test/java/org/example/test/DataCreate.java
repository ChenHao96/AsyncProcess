package org.example.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.test.entity.GradeConfig;
import org.example.test.entity.Product;
import org.example.test.entity.User;
import org.example.test.entity.Wallet;
import org.example.test.mapper.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataCreate extends BaseTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private GradeConfigMapper gradeConfigMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private IntegralRecordMapper integralRecordMapper;

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Before
    public void create00Before() {
        payOrderMapper.delete(null);
        integralRecordMapper.delete(null);
    }

    @Test
    public void create01GradeConfig() {
        gradeConfigMapper.delete(null);
        GradeConfig config = new GradeConfig();
        int ret = 0, count = 10;
        for (int i = 1; i <= count; i++) {
            config.setGrade(i);
            config.setIntegral(i * 100);
            ret += gradeConfigMapper.insert(config);
        }
        Assert.assertEquals(count, ret);
    }

    @Test
    public void create02Product() {
        productMapper.delete(null);
        Product product = new Product();
        Random random = new Random();
        int ret = 0, count = 10;
        for (int i = 1; i <= count; i++) {
            product.setName("商品" + i);
            product.setStock(random.nextInt(1200) + 1);
            product.setSurplusStock(product.getStock());
            product.setIntegral(random.nextInt(200));
            product.setPrice(BigDecimal.valueOf(random.nextDouble() * 800).setScale(2, BigDecimal.ROUND_HALF_UP));
            ret += productMapper.insert(product);
        }
        Assert.assertEquals(count, ret);
    }

    @Test
    public void create03User() {
        userMapper.delete(null);
        User user = new User();
        int ret = 0, count = 100;
        for (int i = 0; i < count; i++) {
            user.setNickName("用户" + i);
            ret += userMapper.insert(user);
        }
        Assert.assertEquals(count, ret);
    }

    @Test
    public void create04Wallet() throws InterruptedException {
        walletMapper.delete(null);
        Random random = new Random();
        List<User> list = userMapper.selectList(new QueryWrapper<>(null, "id"));
        Assert.assertFalse(CollectionUtils.isEmpty(list));
        final CountDownLatch latch = new CountDownLatch(list.size());
        ExecutorService executorService = Executors.newFixedThreadPool(list.size());
        for (User user : list) {
            final int cardCount = random.nextInt(10);
            executorService.submit(() -> {
                int ret = 0;
                try {
                    Wallet wallet = new Wallet();
                    for (int y = 0; y < cardCount; y++) {
                        wallet.setNo(y);
                        wallet.setUserId(user.getId());
                        wallet.setCardName("银行卡" + y);
                        wallet.setBalance(BigDecimal.valueOf(random.nextDouble() * 3000).setScale(2, BigDecimal.ROUND_HALF_UP));
                        ret += walletMapper.insert(wallet);
                    }
                } finally {
                    Assert.assertEquals(cardCount, ret);
                    latch.countDown();
                }
            });
        }
        latch.await();
    }
}

package org.example.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.test.entity.GradeConfig;
import org.example.test.entity.Product;
import org.example.test.entity.User;
import org.example.test.entity.Wallet;
import org.example.test.mapper.GradeConfigMapper;
import org.example.test.mapper.ProductMapper;
import org.example.test.mapper.UserMapper;
import org.example.test.mapper.WalletMapper;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Random;

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

    @Test
    public void create01GradeConfig() {
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
        Product product = new Product();
        Random random = new Random();
        int ret = 0, count = 10;
        for (int i = 1; i <= count; i++) {
            product.setName("商品" + i);
            product.setStock(random.nextInt(20));
            product.setSurplusStock(product.getStock());
            product.setIntegral(random.nextInt(200));
            product.setPrice(BigDecimal.valueOf(random.nextDouble() * 1000).setScale(2, BigDecimal.ROUND_HALF_UP));
            ret += productMapper.insert(product);
        }
        Assert.assertEquals(count, ret);
    }

    @Test
    public void create03User() {
        User user = new User();
        int ret = 0, count = 100;
        for (int i = 0; i < count; i++) {
            user.setNickName("用户" + i);
            ret += userMapper.insert(user);
        }
        Assert.assertEquals(count, ret);
    }

    @Test
    public void create04Wallet() {
        int ret = 0;
        Wallet wallet = new Wallet();
        Random random = new Random();
        int cardCount = random.nextInt(10);
        for (User user : userMapper.selectList(new QueryWrapper<>(null, "id"))) {
            for (int y = 0; y < cardCount; y++) {
                wallet.setNo(y);
                wallet.setUserId(user.getId());
                wallet.setCardName("银行卡" + y);
                wallet.setBalance(BigDecimal.valueOf(random.nextDouble() * 2000).setScale(2, BigDecimal.ROUND_HALF_UP));
                ret += walletMapper.insert(wallet);
            }
            Assert.assertEquals(cardCount, ret);
            cardCount = random.nextInt(10);
            ret = 0;
        }
    }
}

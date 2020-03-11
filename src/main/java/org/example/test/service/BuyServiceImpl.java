package org.example.test.service;

import org.example.test.entity.User;
import org.example.test.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.UUID;

@Service
public class BuyServiceImpl implements BuyService {

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public String panicBuying(Integer userId, Integer productId, Integer productCount) {

        //校验用户有效性
        User user = userMapper.selectById(userId);
        if (user == null) return null;

        final int databaseUpdateRow = 2;
        //1.锁定商品库存
        int updateCount = productService.tryStock(productId, productCount);
        //2.添加商品订单
        String orderNumber = UUID.randomUUID().toString();
        updateCount += payOrderService.addPayOrderRecord(userId, productId, productCount, orderNumber);
        if (updateCount != databaseUpdateRow) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            orderNumber = null;
        }

        return orderNumber;
    }
}

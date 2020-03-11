package org.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.test.entity.PayOrder;
import org.example.test.entity.Product;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.example.test.mapper.PayOrderMapper;
import org.example.test.mapper.ProductMapper;
import org.example.test.service.PayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PayOrderServiceImpl implements PayOrderService {

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    private ProductMapper productMapper;

    private int addPayOrderRecord(PayOrder payOrder) {
        Product product = productMapper.selectById(payOrder.getProductId());
        if (product != null && (product.getSurplusStock() - product.getCommitStock()) >= payOrder.getProductCount()) {
            payOrder.setIntegral(product.getIntegral() * payOrder.getProductCount());
            payOrder.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(payOrder.getProductCount())));
            return payOrderMapper.insert(payOrder);
        }
        return 0;
    }

    public int addPayOrderRecord(Integer userId, Integer productId, Integer productCount, String orderNumber) {
        PayOrder payOrder = new PayOrder();
        payOrder.setUserId(userId);
        payOrder.setProductId(productId);
        payOrder.setOrderNumber(orderNumber);
        payOrder.setProductCount(productCount);
        payOrder.setStatus(PayOrderStatusEnum.CREATE);
        return addPayOrderRecord(payOrder);
    }

    @Override
    public PayOrder queryPayOrderByNumber(String orderNumber) {
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_number", orderNumber);
        return payOrderMapper.selectOne(queryWrapper);
    }

    public int updatePayOrder(PayOrder payOrder) {
        return payOrderMapper.updateById(payOrder);
    }
}

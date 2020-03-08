package org.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.test.entity.PayOrder;
import org.example.test.entity.Product;
import org.example.test.entity.enums.PayOrderStatusEnum;
import org.example.test.mapper.PayOrderMapper;
import org.example.test.mapper.ProductMapper;
import org.example.test.service.PayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Future;

@Service
public class PayOrderServiceImpl implements PayOrderService {

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Async
    @Override
    public Future<PayOrder> addPayOrderRecord(Integer userId, Integer productId, Integer productCount) {
        PayOrder payOrder = new PayOrder();
        payOrder.setUserId(userId);
        payOrder.setProductId(productId);
        payOrder.setProductCount(productCount);
        payOrder.setStatus(PayOrderStatusEnum.CREATE);
        payOrder.setOrderNumber(UUID.randomUUID().toString());
        if (!addPayOrderRecord(payOrder)) payOrder = null;
        return new AsyncResult<>(payOrder);
    }

    private boolean addPayOrderRecord(PayOrder payOrder) {
        Product product = productMapper.selectById(payOrder.getProductId());
        if (product != null) {
            payOrder.setIntegral(product.getIntegral() * payOrder.getProductCount());
            payOrder.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(payOrder.getProductCount())));
            return payOrderMapper.insert(payOrder) == 1;
        }
        return false;
    }

    public boolean addPayOrderRecord(Integer userId, Integer productId, Integer productCount, String orderNumber) {
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

    @Async
    @Override
    public Future<PayOrderStatusEnum> updatePayOrder(PayOrder payOrder) {
        PayOrderStatusEnum result = PayOrderStatusEnum.FAILED;
        PayOrder order = queryPayOrderByNumber(payOrder.getOrderNumber());
        if (order != null) {
            result = order.getStatus();
            if (PayOrderStatusEnum.CREATE.equals(result)) {
                boolean userId = order.getUserId().equals(payOrder.getUserId());
                boolean productId = order.getProductId().equals(payOrder.getProductId());
                boolean productCount = order.getProductCount().equals(payOrder.getProductCount());
                boolean integral = order.getIntegral().equals(payOrder.getIntegral());
                boolean totalPrice = order.getTotalPrice().compareTo(payOrder.getTotalPrice()) == 0;
                if (userId && productId && productCount && integral && totalPrice) {
                    order.setStatus(PayOrderStatusEnum.PAID);
                    if (payOrderMapper.updateById(order) != 1) {
                        result = PayOrderStatusEnum.FAILED;
                    }
                }
            }
        }
        return new AsyncResult<>(result);
    }

    public int updatePayOrder2(PayOrder payOrder) {
        return payOrderMapper.updateById(payOrder);
    }
}

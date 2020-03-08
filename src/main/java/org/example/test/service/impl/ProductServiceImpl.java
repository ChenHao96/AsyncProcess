package org.example.test.service.impl;

import org.example.test.entity.Product;
import org.example.test.mapper.ProductMapper;
import org.example.test.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Async
    @Override
    public Future<Boolean> updateProductTryStock(Integer productId, Integer count) {
        int updateRow = 0;
        Product product = productMapper.selectById(productId);
        if (product != null) {
            Integer commit = product.getCommitStock();
            if (product.getSurplusStock() - commit >= count) {
                product.setCommitStock(commit + count);
                updateRow = productMapper.updateById(product);
            }
        }
        return new AsyncResult<>(updateRow == 1);
    }

    @Override
    public int commitStock2(Integer productId, Integer productCount) {
        Product product = productMapper.selectById(productId);
        if (product != null && product.getSurplusStock() >= productCount) {
            product.setSurplusStock(product.getSurplusStock() - productCount);
            return productMapper.updateById(product);
        }
        return 0;
    }
}

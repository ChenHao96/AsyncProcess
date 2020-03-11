package org.example.test.service.impl;

import org.example.test.entity.Product;
import org.example.test.mapper.ProductMapper;
import org.example.test.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    public int tryStock(Integer productId, Integer count) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            Integer commit = product.getCommitStock();
            if (product.getSurplusStock() - commit >= count) {
                product.setCommitStock(commit + count);
                return productMapper.updateById(product);
            }
        }
        return 0;
    }

    @Override
    public int commitStock(Integer productId, Integer productCount) {
        Product product = productMapper.selectById(productId);
        if (product != null && product.getSurplusStock() >= productCount) {
            product.setCommitStock(product.getCommitStock() - productCount);
            product.setSurplusStock(product.getSurplusStock() - productCount);
            return productMapper.updateById(product);
        }
        return 0;
    }

    @Override
    public int cancelStock(Integer productId, Integer productCount) {
        Product product = productMapper.selectById(productId);
        if (product != null && product.getCommitStock() >= productCount) {
            product.setCommitStock(product.getCommitStock() - productCount);
            return productMapper.updateById(product);
        }
        return 0;
    }
}

package org.example.test.service;

public interface ProductService {

    int tryStock(Integer productId, Integer count);

    int commitStock(Integer productId, Integer productCount);

    int cancelStock(Integer productId, Integer productCount);
}

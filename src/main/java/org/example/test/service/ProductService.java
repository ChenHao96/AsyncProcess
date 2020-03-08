package org.example.test.service;

import java.util.concurrent.Future;

public interface ProductService {

    Future<Boolean> updateProductTryStock(Integer productId, Integer count);

    int commitStock2(Integer productId, Integer productCount);
}

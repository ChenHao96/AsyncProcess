package org.example.test.service;

import java.math.BigDecimal;
import java.util.concurrent.Future;

public interface WalletService {

    Future<Boolean> payOrderDeduction(BigDecimal money, Integer userId);

    boolean payOrderDeduction(Integer userId, BigDecimal money);
}

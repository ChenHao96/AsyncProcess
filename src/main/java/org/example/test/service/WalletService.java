package org.example.test.service;

import java.math.BigDecimal;

public interface WalletService {

    /**
     * 钱包扣款
     *
     * @param money  金额
     * @param userId 用户id
     * @return 用户钱包的id
     */
    Integer tryDeduction(BigDecimal money, Integer userId);

    int commitDeduction(Integer walletId, BigDecimal money);

    int cancelDeduction(Integer walletId, BigDecimal money);
}

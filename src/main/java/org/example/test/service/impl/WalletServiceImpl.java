package org.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.test.entity.Wallet;
import org.example.test.mapper.WalletMapper;
import org.example.test.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletMapper walletMapper;

    public Integer tryDeduction(BigDecimal money, Integer userId) {
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.ge("balance", money);
        queryWrapper.orderByAsc("no");
        List<Wallet> list = walletMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            for (Wallet wallet : list) {
                if (wallet.getBalance().subtract(wallet.getCommitBalance()).compareTo(money) >= 0) {
                    wallet.setCommitBalance(wallet.getCommitBalance().add(money));
                    if (walletMapper.updateById(wallet) == 1) {
                        return wallet.getId();
                    }
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public int commitDeduction(Integer walletId, BigDecimal money) {
        Wallet wallet = walletMapper.selectById(walletId);
        if (wallet != null && wallet.getBalance().compareTo(money) >= 0) {
            wallet.setCommitBalance(wallet.getCommitBalance().subtract(money));
            wallet.setBalance(wallet.getBalance().subtract(money));
            return walletMapper.updateById(wallet);
        }
        return 0;
    }

    @Override
    public int cancelDeduction(Integer walletId, BigDecimal money) {
        Wallet wallet = walletMapper.selectById(walletId);
        if (wallet != null && wallet.getCommitBalance().compareTo(money) >= 0) {
            wallet.setCommitBalance(wallet.getCommitBalance().subtract(money));
            return walletMapper.updateById(wallet);
        }
        return 0;
    }
}

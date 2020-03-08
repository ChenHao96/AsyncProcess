package org.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.test.entity.Wallet;
import org.example.test.mapper.WalletMapper;
import org.example.test.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletMapper walletMapper;

    @Async
    @Override
    public Future<Boolean> payOrderDeduction(BigDecimal money, Integer userId) {
        boolean result = false;
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByAsc("no");
        List<Wallet> list = walletMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            for (Wallet wallet : list) {
                if (wallet.getBalance().subtract(wallet.getCommitBalance()).compareTo(money) >= 0) {
                    wallet.setCommitBalance(wallet.getCommitBalance().add(money));
                    result = walletMapper.updateById(wallet) == 1;
                    break;
                }
            }
        }
        return new AsyncResult<>(result);
    }

    @Override
    public boolean payOrderDeduction(Integer userId, BigDecimal money) {
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByAsc("no");
        List<Wallet> list = walletMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            for (Wallet wallet : list) {
                if (wallet.getBalance().compareTo(money) >= 0) {
                    wallet.setBalance(wallet.getBalance().subtract(money));
                    return walletMapper.updateById(wallet) == 1;
                }
            }
        }
        return false;
    }
}

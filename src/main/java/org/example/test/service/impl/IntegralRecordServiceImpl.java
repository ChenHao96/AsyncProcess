package org.example.test.service.impl;

import org.example.test.entity.IntegralRecord;
import org.example.test.entity.User;
import org.example.test.entity.enums.IntegralOrderTypeEnum;
import org.example.test.mapper.IntegralRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntegralRecordServiceImpl implements IntegralRecordService {

    @Autowired
    private IntegralRecordMapper integralRecordMapper;

    public int addIntegralRecord2(User user, Integer orderId, Integer integral, IntegralOrderTypeEnum orderType) {
        IntegralRecord record = new IntegralRecord();
        record.setOrderId(orderId);
        record.setIntegral(integral);
        record.setUserId(user.getId());
        record.setOrderType(orderType);
        record.setHistory(user.getIntegral() + integral);
        return integralRecordMapper.insert(record);
    }
}

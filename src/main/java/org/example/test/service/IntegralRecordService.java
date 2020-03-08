package org.example.test.service;

import org.example.test.entity.User;
import org.example.test.entity.enums.IntegralOrderTypeEnum;

public interface IntegralRecordService {

    int addIntegralRecord2(User user, Integer orderId, Integer integral, IntegralOrderTypeEnum orderType);
}

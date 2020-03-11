package org.example.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.test.entity.GradeConfig;
import org.example.test.entity.User;
import org.example.test.mapper.GradeConfigMapper;
import org.example.test.mapper.UserMapper;
import org.example.test.service.GradeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradeConfigServiceImpl implements GradeConfigService {

    @Autowired
    private GradeConfigMapper gradeConfigMapper;

    @Autowired
    private UserMapper userMapper;

    public void updateUserGrade(User user, Integer integral) {
        if (user == null) return;
        QueryWrapper<GradeConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("integral", user.getIntegral() + integral);
        queryWrapper.gt("grade", user.getGrade());
        queryWrapper.orderByDesc("grade");
        queryWrapper.last("limit 1");
        GradeConfig config = gradeConfigMapper.selectOne(queryWrapper);
        if (config != null) {
            if (user.getGrade() < config.getGrade()) {
                user.setGrade(config.getGrade());
            }
        }
    }
}

package org.example.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.example.test.entity.User;

public interface UserMapper extends BaseMapper<User> {

    @Update("update users set integral = integral + #{integral}, order_count = order_count + 1, version = version + 1  where id = #{id}")
    int updateUserIntegral(User user);
}

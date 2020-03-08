package org.example.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.example.test.entity.enums.IntegralOrderTypeEnum;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("integral_record")
public class IntegralRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer integral;

    private Integer history;

    private Integer orderId;

    private IntegralOrderTypeEnum orderType;
}

package org.example.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.experimental.Accessors;
import org.example.test.entity.enums.PayOrderStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@TableName("pay_order")
public class PayOrder implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer productId;

    private Integer userId;

    private String orderNumber;

    private Integer integral;

    private Integer productCount;

    private PayOrderStatusEnum status;

    private BigDecimal totalPrice;

    @Version
    private Integer version;
}

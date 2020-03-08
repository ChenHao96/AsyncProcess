package org.example.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@TableName("products")
public class Product implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private BigDecimal price;

    private Integer stock;

    private Integer surplusStock;

    private Integer commitStock;

    private Integer integral;

    @Version
    private Integer version;
}

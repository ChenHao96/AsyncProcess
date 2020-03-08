package org.example.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("grade_config")
public class GradeConfig implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer grade;

    private Integer integral;
}

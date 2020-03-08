package org.example.test.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderParam {

    private Integer index;

    private Integer userId;

    private Integer productId;

    private Integer productCount;
}

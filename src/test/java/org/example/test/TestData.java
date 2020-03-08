package org.example.test;

import lombok.extern.slf4j.Slf4j;
import org.example.test.mapper.ProductMapper;
import org.example.test.mapper.UserMapper;
import org.example.test.model.OrderParam;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Slf4j
public class TestData extends BaseTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    protected List<OrderParam> testData;

    @Before
    public void testBefore() {
        //TODO：
        testData = Collections.emptyList();
    }
}

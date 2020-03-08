package org.example.test.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum IntegralOrderTypeEnum {
    PRODUCT(1);

    private static final Map<Integer, IntegralOrderTypeEnum> codeLookup;

    static {
        EnumSet<IntegralOrderTypeEnum> enumSet = EnumSet.allOf(IntegralOrderTypeEnum.class);
        codeLookup = new ConcurrentHashMap<>(enumSet.size());
        for (IntegralOrderTypeEnum type : enumSet) codeLookup.put(type.code, type);
    }

    @EnumValue
    private final int code;

    IntegralOrderTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static IntegralOrderTypeEnum fromCode(int code) {
        IntegralOrderTypeEnum data = codeLookup.get(code);
        if (data == null)
            throw new IllegalArgumentException("unknown data type code");
        return data;
    }
}

package org.example.test.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PayOrderStatusEnum {
    CREATE(0), FAILED(1), PAID(2), PENDING(3);

    private static final Map<Integer, PayOrderStatusEnum> codeLookup;

    static {
        EnumSet<PayOrderStatusEnum> enumSet = EnumSet.allOf(PayOrderStatusEnum.class);
        codeLookup = new ConcurrentHashMap<>(enumSet.size());
        for (PayOrderStatusEnum type : enumSet) codeLookup.put(type.code, type);
    }

    @EnumValue
    private final int code;

    PayOrderStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PayOrderStatusEnum fromCode(int code) {
        PayOrderStatusEnum data = codeLookup.get(code);
        if (data == null)
            throw new IllegalArgumentException("unknown data type code");
        return data;
    }
}

package com.notenoughmail.examplemod.core.program;

import java.util.HashMap;
import java.util.Map;

public enum Register {
    r0, r1, r2, r3,
    r4, r5, r6, r7,
    r8, r9, rA, rB,
    rC, rD, rE, rF;

    public static final Map<String, Register> registers = new HashMap<>(16);

    static {
        for (Register reg : values()) {
            registers.put(reg.name(), reg);
        }
    }

}

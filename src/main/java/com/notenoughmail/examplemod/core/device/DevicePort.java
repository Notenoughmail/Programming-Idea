package com.notenoughmail.examplemod.core.device;

import java.util.HashMap;
import java.util.Map;

public enum DevicePort {
    d00, d01, d02, d03, d04, d05, d06, d07,
    d08, d09, d0A, d0B, d0C, d0D, d0E, d0F,
    d10, d11, d12, d13, d14, d15, d16, d17,
    d18, d19, d1A, d1B, d1C, d1D, d1E, d1F,
    d20, d21, d22, d23, d24, d25, d26, d27,
    d28, d29, d2A, d2B, d2C, d2D, d2E, d2F,
    d30, d31, d32, d33, d34, d35, d36, d37,
    d38, d39, d3A, d3B, d3C, d3D, d3E, d3F;

    public static final Map<String, DevicePort> ports = new HashMap<>(8 * 8);

    static {
        for (DevicePort port : values()) {
            ports.put(port.name(), port);
        }
    }

}

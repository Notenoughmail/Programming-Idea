package com.notenoughmail.examplemod.core.network;

import java.util.HashMap;
import java.util.Map;

public enum NetworkChannel {
    c0, c1, c2, c3,
    c4, c5, c6, c7;

    public static final Map<String, NetworkChannel> channels = new HashMap<>(8);

    static {
        for  (NetworkChannel channel : values()) {
            channels.put(channel.name(), channel);
        }
    }
}

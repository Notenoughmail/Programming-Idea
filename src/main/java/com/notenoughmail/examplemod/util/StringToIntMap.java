package com.notenoughmail.examplemod.util;

import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;

public class StringToIntMap {

    private String[] keys;
    private int[] values;
    private int capableSize;
    private int currentSize;

    public StringToIntMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            initialCapacity = 1;
        }
        keys = new String[initialCapacity];
        values = new int[initialCapacity];
        capableSize = initialCapacity;
        currentSize = 0;
    }

    public StringToIntMap() {
        this(4);
    }

    public void put(String key, int value) {
        keys[currentSize] = key;
        values[currentSize] = value;
        currentSize++;
        if (currentSize == capableSize) {
            capableSize *= 2;
            keys = Arrays.copyOf(keys, capableSize);
            values = Arrays.copyOf(values, capableSize);
        }
    }

    public int get(String key) {
        for (int i = 0 ; i < currentSize ; i++) {
            if (key.equals(keys[i])) {
                return values[i];
            }
        }
        return -1;
    }

    public CompoundTag writeToNbt() {
        final CompoundTag tag = new CompoundTag();
        for (int i = 0 ; i < currentSize ; i++) {
            tag.putInt(keys[i], values[i]);
        }
        return tag;
    }

    public static StringToIntMap readFromNbt(CompoundTag tag) {
        final StringToIntMap map = new StringToIntMap(tag.size());
        for (String label : tag.getAllKeys()) {
            map.put(label, tag.getInt(label));
        }
        return map;
    }
}

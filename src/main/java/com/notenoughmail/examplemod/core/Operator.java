package com.notenoughmail.examplemod.core;

@FunctionalInterface
public interface Operator {

    void invoke(Line line, Object[] args, Program program);
}

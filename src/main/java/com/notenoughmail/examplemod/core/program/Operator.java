package com.notenoughmail.examplemod.core.program;

@FunctionalInterface
public interface Operator {

    void invoke(Line line, Object[] args, Program program);
}

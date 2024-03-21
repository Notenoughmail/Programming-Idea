package com.notenoughmail.examplemod.core.program;

import com.notenoughmail.examplemod.core.program.line.Line;

@FunctionalInterface
public interface Operator {

    void invoke(Line line, Object[] args, Program program);
}

package com.notenoughmail.examplemod.core;

public class EarlyEndLine extends Line {

    private static Object[] nul = new Object[]{Operation.nil};

    public EarlyEndLine(Program program, int lineNumber) {
        super(program, new Object[]{Operation.nil}, Operation.nil, lineNumber);
    }
}

package com.notenoughmail.examplemod.core;

public class Line {

    private final Program parentProgram;
    private final Object[] args;
    private final Operation operation;
    private final int lineNumber;

    public Line(Program program, Object[] args, Operation operation, int lineNumber) {
        this.parentProgram = program;
        this.args = args;
        this.operation = operation;
        this.lineNumber = lineNumber;
        if (this.args[0] != this.operation) {
            throw new IllegalArgumentException("First argument must be the line's operation!");
        }
    }

    public Program getProgram() {
        return parentProgram;
    }

    public Object[] getArgs() {
        return args;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void run() {
        operation.invoke(this);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        appendString(builder);
        return builder.toString();
    }

    public void appendString(StringBuilder builder) {
        builder.append(getLineNumber(lineNumber));
        builder.append("|");
        for (Object arg : args) {
            builder.append(" ");
            builder.append(arg.toString());
        }
    }

    public static String getLineNumber(int lineNumber) {
        String transitional = Integer.toString(lineNumber, 16).toUpperCase();
        if (transitional.length() == 1) {
            transitional = "0" + transitional;
        }
        return transitional;
    }
}

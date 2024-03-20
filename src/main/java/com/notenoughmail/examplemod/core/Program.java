package com.notenoughmail.examplemod.core;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Program {

    private static final double[] emptyRegisters = new double[]{
            0D, 0D, 0D, 0D,
            0D, 0D, 0D, 0D,
            0D, 0D, 0D, 0D,
            0D, 0D, 0D, 0D
    };
    public static final String ALIAS = "alias";

    private final Map<String, Object> aliases;
    private final Line[] lines;
    public final double[] registers;
    public final String name;

    public Program(String program, String name) {
        this.aliases = new HashMap<>();
        aliases.put("true", 1D);
        aliases.put("false", 0D);
        this.registers = Arrays.copyOf(emptyRegisters, 16);
        this.name = name;
        this.lines = processProgram(program, this);
    }

    public Map<String, Object> getAliases() {
        return aliases;
    }

    public void run() {
        for (Line line : lines) {
            line.run();
        }
    }

    public void sendError(String error) {
        // TODO: Implement
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Program: ");
        builder.append(name);
        builder.append("\n");
        builder.append("  |\n");
        for (Line line : lines) {
            line.appendString(builder);
            builder.append("\n");
        }
        builder.append("  |");
        return builder.toString();
    }

    static Line[] processProgram(String programString, Program program) {
        final String[] lines = programString.split("\n");
        final List<Line> programLines = new ArrayList<>();
        int lineNumber = 0;
        for (String line : lines) {
            final String processed = line.trim();
            if (processed.isEmpty() || processed.charAt(0) == '#') {
                continue;
            }
            final Line processedLine = processLine(processed, program, lineNumber);
            if (processedLine instanceof EarlyEndLine) {
                break;
            }
            if (processedLine != null) {
                lineNumber++;
                programLines.add(processedLine);
            }
        }
        return programLines.toArray(new Line[0]);
    }

    @Nullable
    static Line processLine(String line, Program program, int lineNumber) {
        final String[] args = line.split(" ");
        if (ALIAS.equals(args[0])) {
            program.aliases.put(args[1], processArg(args[2], program));
            return null;
        } else {
            final Operation op = Operation.operations.get(args[0]);
            if (op != null) {
                if (op.argCount() != args.length) {
                    program.sendError("Line[" + Line.getLineNumber(lineNumber) + "] does not have a valid number of arguments for the given operation: " + op.name() + ". Expected: " + op.argCount() + ", given: " + args.length);
                    return new EarlyEndLine(program, lineNumber);
                } else {
                    final Object[] lineArgs = new Object[op.argCount()];
                    lineArgs[0] = op;
                    for (int i = 1 ; i < op.argCount() ; i++) {
                        lineArgs[i] = processArg(args[i], program);
                    }
                    return new Line(program, lineArgs, op, lineNumber);
                }
            }
        }
        return null;
    }

    static Object processArg(String arg, Program program) {
        if (arg.charAt(0) == 'r') {
            final Register reg = Register.registers.get(arg);
            if (reg != null) {
                return reg;
            }
        } else if (arg.charAt(0) == 'd') {
            // DevicePort stuff
        } else if (arg.charAt(0) == 'c') {
            // Network channel stuff
        } else {
            try {
                return Double.valueOf(arg);
            } catch (Exception ignored) {
            }
            return 0D; // I dunno
        }
        return 0D;
    }

    public static double getValue(Object arg, Program program) {
        if (arg instanceof Register reg) {
            return program.registers[reg.ordinal()];
        } else if (arg instanceof Number number) {
            return number.doubleValue();
        } else {
            return 0D; // DevicePort and NetworkChannel stuff
        }
    }
}

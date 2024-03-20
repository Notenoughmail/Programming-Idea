package com.notenoughmail.examplemod.core;

import java.util.HashMap;
import java.util.Map;

// TODO: Redo Program#sendError() to take a MutableComponent
public enum Operation {
    nil(1, (line, args, program) -> program.sendError("Operation[nil] should never be called!")),
    add(4, (line, args, program) -> {
        if (args[1] instanceof Register reg) {
            program.registers[reg.ordinal()] = Program.getValue(args[2], program) + Program.getValue(args[3], program);
        } else {
            program.sendError("Operation[add] requires the first argument to be either a Register, DevicePort, or NetworkChannel, was a " + args[1].getClass().getName());
        }
    }),
    sub(4, (line, args, program) -> {
        if (args[1] instanceof Register reg) {
            program.registers[reg.ordinal()] = Program.getValue(args[2], program) - Program.getValue(args[3], program);
        } else {
            program.sendError("Operation[sub] requires the first argument to be either a Register, DevicePort, or NetworkChannel, was a " + args[1].getClass().getName());
        }
    }),
    mul(4, (line, args, program) -> {
        if (args[1] instanceof Register reg) {
            program.registers[reg.ordinal()] = Program.getValue(args[2], program) * Program.getValue(args[3], program);
        } else {
            program.sendError("Operation[mul] requires the first argument to be either a Register, DevicePort, or NetworkChannel, was a " + args[1].getClass().getName());
        }
    }),
    div(4, (line, args, program) -> {
        if (args[1] instanceof Register reg) {
            program.registers[reg.ordinal()] = Program.getValue(args[2], program) / Program.getValue(args[3], program);
        } else {
            program.sendError("Operation[div] requires the first argument to be either a Register, DevicePort, or NetworkChannel, was a " + args[1].getClass().getName());
        }
    }),
    pow(4, (line, args, program) -> {
        if (args[1] instanceof Register reg) {
            program.registers[reg.ordinal()] = Math.pow(Program.getValue(args[2], program), Program.getValue(args[3], program));
        } else {
            program.sendError("Operation[pow] requires the first argument to be either a Register, DevicePort, or NetworkChannel, was a " + args[1].getClass().getName());
        }
    }),
    set(3, (line, args, program) -> {
        if (args[1] instanceof Register reg) {
            program.registers[reg.ordinal()] = Program.getValue(args[2], program);
        } else {
            program.sendError("Operation[set] requires the first argument to be either a Register, DevicePort, or NetworkChannel, was a " + args[1].getClass().getName());
        }
    }),
    eql(4, (line, args, program) -> {
        if (args[1] instanceof Register reg) {
            program.registers[reg.ordinal()] = Program.getValue(args[2], program) == Program.getValue(args[3], program) ? 1D : 0D;
        } else {
            program.sendError("Operation[eql] requires the first argument");
        }
    }),
    eqz(3, (line, args, program) -> {

    }),
    ltz(3, (line, args, program) -> {

    }),
    gtz(3, (line, args, program) -> {

    });

    public static final Map<String, Operation> operations = new HashMap<>();

    static {
        for (Operation op : values()) {
            operations.put(op.name(), op);
        }
        operations.remove(nil.name());
    }

    private final int argCount;
    private final Operator operator;

    Operation(int argCount, Operator operator) {
        this.argCount = argCount;
        this.operator = operator;
    }

    public int argCount() {
        return argCount;
    }

    public void invoke(Line line) {
        operator.invoke(line, line.getArgs(), line.getProgram());
    }
}

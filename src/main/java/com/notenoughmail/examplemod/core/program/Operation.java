package com.notenoughmail.examplemod.core.program;

import com.notenoughmail.examplemod.core.device.DevicePort;
import com.notenoughmail.examplemod.core.network.NetworkChannel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.HashMap;
import java.util.Map;

public enum Operation {
    nil(1, (line, args, program) -> program.sendError(Component.translatable("message.examplemod.nil_called"))),
    add(4, (line, args, program) -> {
        final double value = Program.getValue(args[2], program) + Program.getValue(args[3], program);
        defaultSetValue(args[1], value, "add", line);
    }),
    sub(4, (line, args, program) -> {
        final double value = Program.getValue(args[2], program) - Program.getValue(args[3], program);
        defaultSetValue(args[1], value, "sub", line);
    }),
    mul(4, (line, args, program) -> {
        final double value = Program.getValue(args[2], program) * Program.getValue(args[3], program);
        defaultSetValue(args[1], value, "mul", line);
    }),
    div(4, (line, args, program) -> {
        final double value = Program.getValue(args[2], program) / Program.getValue(args[3], program);
        defaultSetValue(args[1], value, "div", line);
    }),
    pow(4, (line, args, program) -> {
        final double value = Math.pow(Program.getValue(args[2], program), Program.getValue(args[3], program));
        defaultSetValue(args[1], value, "pow", line);
    }),
    set(3, (line, args, program) -> {
        final double value = Program.getValue(args[2], program);
        defaultSetValue(args[1], value, "set", line);
    }),
    eql(4, (line, args, program) -> {
        final double value = Program.getValue(args[2], program) == Program.getValue(args[3], program) ? 1D : 0D;
        defaultSetValue(args[1], value, "eql", line);
    }),
    eqz(3, (line, args, program) -> {
        final double value = Program.getValue(args[2], program) == 0 ? 1D : 0D;
        defaultSetValue(args[1], value, "eqz", line);
    }),
    ltz(3, (line, args, program) -> {
        final double value = Program.getValue(args[2], program) < 0 ? 1D : 0D;
        defaultSetValue(args[1], value, "ltz", line);
    }),
    gtz(3, (line, args, program) -> {
        final double value = Program.getValue(args[2], program) > 0 ? 1D : 0D;
        defaultSetValue(args[1], value, "gtz", line);
    }),
    mod(4, (line, args, program) -> {
        final double value = Program.getValue(args[2], program) % Program.getValue(args[2], program);
        defaultSetValue(args[1], value, "mod", line);
    }),
    and(4, (line, args, program) -> {
        final double value = (long) Program.getValue(args[2], program) & (long) Program.getValue(args[3], program);
        defaultSetValue(args[1], value, "and", line);
    }),
    xor(4, (line, args, program) -> {
        final double value = (long) Program.getValue(args[2], program) ^ (long) Program.getValue(args[3], program);
        defaultSetValue(args[1], value, "xor", line);
    }),
    not(3, (line, args, program) -> {
        final double value = ~ (long) Program.getValue(args[2], program);
        defaultSetValue(args[1], value, "not", line);
    }),
    bsl(4, (line, args, program) -> {
        final double value = (long) Program.getValue(args[2], program) << (int) Program.getValue(args[3], program);
        defaultSetValue(args[1], value, "bsl", line);
    }),
    bsr(4, (line, args, program) -> {
        final double value = (long) Program.getValue(args[2], program) >> (int) Program.getValue(args[3], program);
        defaultSetValue(args[1], value, "bsr", line);
    }),
    or(4, (line, args, program) -> {
        final double value = (long) Program.getValue(args[2], program) | (long) Program.getValue(args[3], program);
        defaultSetValue(args[1], value, "or", line);
    }),
    con(2, (line, args, program) -> program.log(Component.translatable("message.examplemod.con", args[1]))),
    abs(3, (line, args, program) -> {
        final double value = Math.abs(Program.getValue(args[2], program));
        defaultSetValue(args[1], value, "abs", line);
    }),
    flr(3, (line, args, program) -> {
        final double value = Math.floor(Program.getValue(args[2], program));
        defaultSetValue(args[1], value, "flr", line);
    }),
    cel(3, (line, args, program) -> {
       final double value = Math.ceil(Program.getValue(args[2], program));
        defaultSetValue(args[1], value, "cel", line);
    }),
    log(3, (line, args, program) -> {
        final double value = Math.log10(Program.getValue(args[2], program));
        defaultSetValue(args[1], value, "log", line);
    }),
    max(4, (line, args, program) -> {
        final double value = Math.max(Program.getValue(args[2], program), Program.getValue(args[3], program));
        defaultSetValue(args[1], value, "max", line);
    }),
    min(4, (line, args, program) -> {
        final double value = Math.min(Program.getValue(args[2], program), Program.getValue(args[3], program));
        defaultSetValue(args[1], value, "min", line);
    }),
    ln(3, (line, args, program) -> {
        final double value = Math.log(Program.getValue(args[2], program));
        defaultSetValue(args[1], value, "ln", line);
    }),
    sin(3, (line, args, program) -> {
        final double value = Math.sin(Program.getValue(args[2], program));
        defaultSetValue(args[1], value, "sin", line);
    }),
    cos(3, (line, args, program) -> {
        final double value = Math.cos(Program.getValue(args[2], program));
        defaultSetValue(args[1], value, "cos", line);
    }),
    tan(3, (line, args, program) -> {
        final double value = Math.tan(Program.getValue(args[2], program));
        defaultSetValue(args[1], value, "tan", line);
    }),
    cbr(3, (line, args, program) -> {
        final double value = Math.cbrt(Program.getValue(args[2], program));
        defaultSetValue(args[1], value, "cbr", line);
    }),
    sqr(3, (line, args, program) -> {
        final double value = Math.sqrt(Program.getValue(args[2], program));
        defaultSetValue(args[1], value, "sqr", line);
    }),
    neg(3, (line, args, program) -> {
        final double value = -Program.getValue(args[2], program);
        defaultSetValue(args[1], value, "neg", line);
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

    static boolean setValue(Object arg, Program program, double value) {
        if (arg instanceof Register reg) {
            program.registers[reg.ordinal()] = value;
            return false;
        } else if (arg instanceof DevicePort port) {
            // TODO: Implement devices
        } else if (arg instanceof NetworkChannel channel) {
            // TODO: Implement networks
        }
        return true;
    }

    static void defaultSetValue(Object arg, double value, String op, Line line) {
        if (setValue(arg, line.getProgram(), value)) {
            line.getProgram().sendError(requiresRDC(op, arg), line.getLineNumber());
        }
    }

    static MutableComponent requiresRDC(String op, Object arg) {
        return Component.translatable("message.examplemod.requires_rdc", op, arg.getClass().getName());
    }

    public int argCount() {
        return argCount;
    }

    public void invoke(Line line) {
        operator.invoke(line, line.getArgs(), line.getProgram());
    }
}

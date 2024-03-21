package com.notenoughmail.examplemod.core.program;

import com.notenoughmail.examplemod.core.device.DevicePort;
import com.notenoughmail.examplemod.core.network.NetworkChannel;
import com.notenoughmail.examplemod.core.program.line.Line;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <br>{@link Operation#nil}: Legacy empty operation representing a "null" operation. Not able to written in programs. Errors the program when called
 * <br>{@link Operation#label}: Internal operator that is used to hold the position of a jump label in a program. Written in programs as: <strong>{@code <label>:}</strong>
 * <br>{@link Operation#add}: Adds the 2nd & 3rd argument together and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#sub}: Subtracts the 3rd argument from the 2nd argument and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#mul}: Multiplies the 2nd and 3rd arguments and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#div}: Divides the 2nd argument by the 3rd argument and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#pow}: Takes the power of the 2nd argument to the 3rd argument (arg2^arg3) and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#set}: Takes the value in the 2nd argument and puts in into the register/port/channel in the 1st argument
 * <br>{@link Operation#eql}: Equates the 2nd and 3rd arguments, if equal, puts a true value (1.0), else false (0.0), into the register/port/channel of the 1st argument
 * <br>{@link Operation#eqz}: Equates the 2nd argument to zero, if equal, puts a true value (1.0), else false (0.0), into the register/port/channel of the 1st argument
 * <br>{@link Operation#ltz}: Compares the 2nd argument to 0, if less than, puts a true value (1.0), else false (0.0) into the register/port/channel of the 1st argument
 * <br>{@link Operation#gtz}: Compares the 2nd argument to 0, if greater than, puts a true value (1.0), else false (0.0) into the register/port/channel in the 1st argument
 * <br>{@link Operation#mod}: Performs a modulus operation between the 2nd and 3rd arguments (arg2 % arg3) and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#and}: Performs a bitwise and on the signed-64-bit-integer representation of the 2nd and 3rd arguments and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#xor}: Performs a bitwise xor on the signed-64-bit-integer representation of the 2nd and 3rd arguments and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#not}: Performs a bitwise not on the signed-64-bit-integer representation of the 2nd and 3rd arguments and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#bsl}: Performs a leftward bitshift on the value in the 2nd argument, shifting by the amount in the 3rd argument, and putting the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#bsr}: Performs a rightward bitshift on the value in the 2nd argument, shifting by the amount in the 3rd argument, and putting the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#or}: Performs a bitwise or on the signed-64-bit-integer representation of the 2nd and 3rd arguments and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#con}: Sends the provided arguments to the program's console. Important: aliases will be transformed to their real values
 * <br>{@link Operation#abs}: Takes the absolute value of the 2nd argument and puts it into the register/port/channel in the 1st argument
 * <br>{@link Operation#flr}: Rounds, towards positive infinity, the value in the 2nd argument to the nearest integer less than or equal to the value and puts it into the register/port/channel in the 1st argument
 * <br>{@link Operation#cel}: Rounds, towards negative infinity, the value in the 2nd argument to the nearest integer greater than or equal to the value and puts it into the register/port/channel in the 1st argument
 * <br>{@link Operation#log}: Takes the base-10 log of the 2nd argument and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#max}: Finds the maximum value of the 2nd and 3rd arguments and puts it into the register/port/channel in the 1st argument
 * <br>{@link Operation#min}: Fins the minimum value of the 2nd and 3rd arguments and puts it into the register/port/channel in the 1st argument
 * <br>{@link Operation#ln}: Takes the natural log of the 2nd argument and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#sin}: Takes the sine value of the 2nd argument and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#cos}: Takes the cosine value of the 2nd argument and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#tan}: Takes the tangent value of the 2nd argument and puts the result into the register/port/channel in the 1st  argument
 * <br>{@link Operation#cbr}: Takes the cube root of the 2nd argument and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#sqr}: Takes the square root of the 2nd argument and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#neg}: Takes the negative value of the value in the 2nd argument and puts the result into the register/port/channel in the 1st argument
 * <br>{@link Operation#jmp}: Jumps the program to the label in the 1st argument. Errors the program if label does not exist
 * <br>{@link Operation#prt}: Sends the 1st argument to the console, where the 2nd argument is everything between the end of the prt keyword and the end of the line or the beginning of a comment. Important: aliases will <strong>not</strong> be transformed to their real values
 */
public enum Operation {
    nil(1, false, (line, args, program) -> program.sendError(Component.translatable("message.examplemod.nil_called"))),
    label(1, false, (line, args, program) -> {}),
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
    con(2, (line, args, program) -> program.log(Component.translatable("message.examplemod.con", Arrays.copyOfRange(args, 1, args.length - 1)))),
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
    }),
    jmp(2, (line, args, program) -> program.jumpToLine((String) args[1], line.getLineNumber())),
    prt(2, (line, args, program) -> program.log(Component.translatable("message.examplemod.con", args[1])));

    public static final Map<String, Operation> operations = new HashMap<>();

    static {
        for (Operation op : values()) {
            if (op.writeable) {
                operations.put(op.name(), op);
            }
        }
    }

    public static final Operation[] VALUES = values();

    private final int minArgs;
    private final Operator operator;
    private final boolean writeable;

    Operation(int minArgs, Operator operator) {
        this(minArgs, true, operator);
    }

    Operation(int minArgs, boolean writeable, Operator operator) {
        this.minArgs = minArgs;
        this.operator = operator;
        this.writeable = writeable;
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

    public int minArgs() {
        return minArgs;
    }

    public void invoke(Line line) {
        operator.invoke(line, line.getArgs(), line.getProgram());
    }
}

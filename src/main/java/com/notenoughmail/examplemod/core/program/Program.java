package com.notenoughmail.examplemod.core.program;

import com.notenoughmail.examplemod.core.device.DevicePort;
import com.notenoughmail.examplemod.core.network.NetworkChannel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Program {

    private static final double[] emptyRegisters = new double[]{
            0D, 0D, 0D, 0D,
            0D, 0D, 0D, 0D,
            0D, 0D, 0D, 0D,
            0D, 0D, 0D, 0D
    };
    public static final String ALIAS = "alias";
    /**
     * A regex for validating a string to go into {@link Double#valueOf(String)}, found after reading
     * the relevant javadoc and messing around in <a href="https://regexper.com">Regexper</a> to minify
     * to a size reasonable for this purpose
     */
    private static final Matcher numberPattern = Pattern.compile("^((\\d+(\\.\\d+)?([eE]\\d+)?)|(0[xX]\\p{XDigit}+(\\.\\p{XDigit}+)?([pP]\\p{XDigit}+)?))$").matcher("");

    private static Map<String, Object> initAliases() {
        final Map<String, Object> aliases = new HashMap<>(4);
        aliases.put("true", 1D);
        aliases.put("false", 0D);
        aliases.put("pi", Math.PI);
        aliases.put("e", Math.E);
        return aliases;
    }

    private final Map<String, Object> aliases;
    private final Line[] lines;
    public final double[] registers;
    public final String name;
    @Nullable
    private MutableComponent errorMessage;
    private boolean hasError;
    @Nullable
    public MutableComponent logMessage;

    public Program(String program, String name) {
        this.aliases = initAliases();
        this.registers = Arrays.copyOf(emptyRegisters, 16);
        this.name = name;
        hasError = false;
        this.lines = processProgram(program, this);
    }

    public Map<String, Object> getAliases() {
        return aliases;
    }

    public void run() {
        if (!hasError) {
            for (Line line : lines) {
                line.run();
                if (hasError) {
                    break;
                }
            }
        }
    }

    public void sendError(MutableComponent error, int lineNumber) {
        this.hasError = true;
        this.errorMessage = Component.translatable("message.examplemod.error_on_line", Line.getLineNumber(lineNumber), error);
    }

    public void sendError(MutableComponent error) {
        this.hasError = true;
        this.errorMessage = error;
    }

    @Nullable
    public MutableComponent getError() {
        return errorMessage;
    }

    public void log(MutableComponent message) {
        this.logMessage = message;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Program: ");
        builder.append(name);
        builder.append("\n");
        builder.append("  |\n");
        for (Line line : lines) {
            line.appendToProgram(builder);
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
            if (program.hasError) {
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
        String[] args = line.split(" ");
        if (ALIAS.equals(args[0])) {
            program.aliases.put(args[1], processArg(args[2], program, false));
            return null;
        } else {
            for (int i = 0 ; i < args.length ; i++) {
                final String arg = args[i];
                if (arg.charAt(0) == '#') {
                    args = Arrays.copyOf(args, i + 1);
                    break;
                }
            }
            final Operation op = Operation.operations.get(args[0]);
            if (op != null) {
                if (op.argCount() != args.length) {
                    program.sendError(Component.translatable("message.examplemod.line_has_incorrect_number_of_args", Line.getLineNumber(lineNumber), op.name(), op.argCount(), args.length));
                    return null;
                } else {
                    final Object[] lineArgs = new Object[op.argCount()];
                    lineArgs[0] = op;
                    for (int i = 1 ; i < op.argCount() ; i++) {
                        final Object arg = processArg(args[i], program, op == Operation.con);
                        if (program.hasError) {
                            return null;
                        }
                        lineArgs[i] = arg;
                    }
                    return new Line(program, lineArgs, op, lineNumber);
                }
            }
        }
        return null;
    }

    static Object processArg(String arg, Program program, boolean log) {
        if (log) {
            return arg;
        } else if (arg.charAt(0) == 'r') {
            final Register reg = Register.registers.get(arg);
            if (reg != null) {
                return reg;
            }
        } else if (arg.charAt(0) == 'd') {
            final DevicePort port = DevicePort.ports.get(arg);
            if (port != null) {
                return port;
            }
        } else if (arg.charAt(0) == 'c') {
            final NetworkChannel channel = NetworkChannel.channels.get(arg);
            if (channel != null) {
                return channel;
            }
        } else if (numberPattern.reset(arg).matches()) {
            return Double.valueOf(arg);
        }
        program.sendError(Component.translatable("message.examplemod.could_not_parse_arg", arg));
        return 0D;
    }

    public static double getValue(Object arg, Program program) {
        if (arg instanceof Register reg) {
            return program.registers[reg.ordinal()];
        } else if (arg instanceof Number number) {
            return number.doubleValue();
        } else if (arg instanceof DevicePort port) {
            // TODO: Implement devices
        } else if (arg instanceof NetworkChannel channel) {
            // TODO: Implement networks
        } else {
            program.sendError(Component.translatable("message.examplemod.could_not_retrieve_value", arg));
        }
        return 0D;
    }
}

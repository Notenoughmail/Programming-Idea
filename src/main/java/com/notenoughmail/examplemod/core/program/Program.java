package com.notenoughmail.examplemod.core.program;

import com.notenoughmail.examplemod.core.device.DevicePort;
import com.notenoughmail.examplemod.core.network.NetworkChannel;
import com.notenoughmail.examplemod.core.program.line.JumpLine;
import com.notenoughmail.examplemod.core.program.line.LabelLine;
import com.notenoughmail.examplemod.core.program.line.Line;
import com.notenoughmail.examplemod.util.StringToIntMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
    private boolean hasError = false;
    @Nullable
    public MutableComponent logMessage;
    private int currentLine = 0;
    private final StringToIntMap labels;

    public Program(String program, String name) {
        this.aliases = initAliases();
        this.labels = new StringToIntMap();
        this.registers = Arrays.copyOf(emptyRegisters, 16);
        this.name = name;
        this.lines = processProgram(program, this);
    }

    private Program(CompoundTag tag) {
        this.name = tag.getString("name");
        this.labels = StringToIntMap.readFromNbt(tag.getCompound("labels"));
        this.registers = Arrays.copyOf(emptyRegisters, 16);
        this.aliases = readAliases(tag.getCompound("aliases"));
        this.lines = readLines(tag.getCompound("lines"), this);
    }

    public static Program readFromNbt(CompoundTag tag) {
        return new Program(tag);
    }

    public Map<String, Object> getAliases() {
        return aliases;
    }

    public void run() {
        if (!hasError) {
            for ( ; currentLine < lines.length ; currentLine++) {
                lines[currentLine].run();
                if (hasError) {
                    break;
                }
            }
            currentLine = 0;
        }
    }

    public void jumpToLine(String label, int errorLine) {
        final int i = labels.get(label);
        if (i < 0) {
            sendError(Component.translatable("message.examplemod.label_does_not_exist", Line.getLineNumber(errorLine)));
        } else {
            currentLine = i;
        }
    }

    public void sendError(MutableComponent error, int lineNumber) {
        sendError(Component.translatable("message.examplemod.error_on_line", Line.getLineNumber(lineNumber), error));
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

    public CompoundTag writeToNbt() {
        final CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.put("labels", labels.writeToNbt());
        tag.put("aliases", writeAliases(aliases));
        tag.put("lines", writeLines(lines));
        return tag;
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
            if (processedLine instanceof LabelLine label) {
                program.labels.put(label.getLabel(), lineNumber);
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
            program.aliases.put(args[1], processArg(args[2], program));
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
                if (op.minArgs() > args.length) {
                    program.sendError(Component.translatable("message.examplemod.line_has_incorrect_number_of_args", Line.getLineNumber(lineNumber), op.name(), op.minArgs(), args.length));
                    return null;
                } else if (op == Operation.prt) {
                    return new Line(program, new Object[]{op, line.substring(4, line.length() - 1)}, op, lineNumber);
                } else if (op == Operation.jmp) {
                    return new JumpLine(program, args[1], lineNumber);
                } else {
                    final Object[] lineArgs = new Object[args.length];
                    lineArgs[0] = op;
                    for (int i = 1; i < op.minArgs() ; i++) {
                        final Object arg = processArg(args[i], program);
                        if (program.hasError) {
                            return null;
                        }
                        lineArgs[i] = arg;
                    }
                    return new Line(program, lineArgs, op, lineNumber);
                }
            } else if (args.length == 1){
                final String possibleLabel = args[0];
                if (possibleLabel.length() > 1 && possibleLabel.indexOf(':') == possibleLabel.length() - 1) {
                    return new LabelLine(program, possibleLabel.substring(0, possibleLabel.length() - 2), lineNumber);
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
        } else {
            final Object possiblyAliased = program.aliases.get(arg);
            if (possiblyAliased != null) {
                return possiblyAliased;
            }
        }
        program.sendError(Component.translatable("message.examplemod.could_not_parse_arg", arg));
        return null;
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

    private static Map<String, Object> readAliases(CompoundTag tag) {
        final Map<String, Object> map = new HashMap<>(tag.size());
        for (String alias : tag.getAllKeys()) {
            map.put(alias, readArgValue(tag.getCompound(alias)));
        }
        return map;
    }

    private static CompoundTag writeAliases(Map<String, Object> aliases) {
        final CompoundTag tag = new CompoundTag();
        aliases.forEach((alias, value) -> tag.put(alias, writeArgValue(value)));
        return tag;
    }

    private static Line[] readLines(CompoundTag tag, Program program) {
        final int size = tag.getInt("size");
        final Line[] lines = new Line[size];
        final ListTag list = tag.getList("lines", 10);
        for (int i = 0 ; i < size ; i++) {
            lines[i] = Line.readFromNbt(list.getCompound(i), program);
        }
        return lines;
    }

    private static CompoundTag writeLines(Line[] lines) {
        final CompoundTag tag = new CompoundTag();
        tag.putInt("size", lines.length);
        final ListTag list = new ListTag();
        for (int i = 0 ; i < lines.length ; i++) {
            list.add(i, lines[i].writeToNbt());
        }
        tag.put("lines", list);
        return tag;
    }

    public static Object readArgValue(CompoundTag tag) {
        final byte type = tag.getByte("type");
        if (type == 0) {
            return Operation.operations.get(tag.getString("value"));
        } else if(type == 1) {
            return DevicePort.ports.get(tag.getString("value"));
        } else if(type == 2) {
            return NetworkChannel.channels.get(tag.getString("value"));
        } else if (type == 3) {
            return tag.getDouble("value");
        } else if (type == 4) {
            return tag.getString("value");
        }
        return null; // Should not happen!
    }

    public static Tag writeArgValue(Object arg) {
        final CompoundTag tag = new CompoundTag();
        byte type = -1;
        if (arg instanceof Operation op) {
            type = 0;
            tag.putString("value", op.name());
        } else if (arg instanceof DevicePort port) {
            type = 1;
            tag.putString("value", port.name());
        } else if (arg instanceof NetworkChannel channel) {
            type = 2;
            tag.putString("value", channel.name());
        } else if (arg instanceof Number number) {
            type = 3;
            tag.putDouble("value", number.doubleValue());
        } else if (arg instanceof String str) {
            type = 4;
            tag.putString("value", str);
        }
        tag.putByte("type", type);
        return tag;
    }
}

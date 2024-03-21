package com.notenoughmail.examplemod.core.program.line;

import com.notenoughmail.examplemod.core.program.Operation;
import com.notenoughmail.examplemod.core.program.Program;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class Line {

    protected final Program parentProgram;
    protected final Object[] args;
    protected final Operation operation;
    protected final int lineNumber;

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
        final StringBuilder builder = new StringBuilder("Line[" + getLineNumber(lineNumber) + "]:");
        for (Object arg : args) {
            builder.append(" ");
            builder.append(arg);
        }
        return builder.toString();
    }

    public void appendToProgram(StringBuilder builder) {
        builder.append(getLineNumber(lineNumber));
        builder.append("|");
        for (Object arg : args) {
            builder.append(" ");
            builder.append(arg);
        }
        builder.append("\n");
    }

    public static String getLineNumber(int lineNumber) {
        String transitional = Integer.toString(lineNumber, 16).toUpperCase();
        if (transitional.length() == 1) {
            transitional = "0" + transitional;
        }
        return transitional;
    }

    public CompoundTag writeToNbt() {
        final CompoundTag tag = new CompoundTag();
        tag.putByte("type", (byte) 0);
        tag.putInt("operation", operation.ordinal());
        tag.putInt("lineNumber", lineNumber);
        final ListTag argsList = new ListTag();
        for (int i = 1 ; i < args.length ; i++) {
            argsList.add(i - 1, Program.writeArgValue(args[i]));
        }
        tag.put("args", argsList);
        return tag;
    }

    public static Line readFromNbt(CompoundTag tag, Program program) {
        final byte type = tag.getByte("type");
        final Operation op = Operation.VALUES[tag.getInt("operation")];
        final int lineNumber = tag.getInt("lineNumber");
        if (type == 0) {
            final Object[] args = new Object[op.minArgs()];
            args[0] = op;
            final ListTag list = tag.getList("args", 10);
            for (int i = 1; i < op.minArgs() ; i++) {
                args[i] = Program.readArgValue(list.getCompound(i - 1));
            }
            return new Line(program, args, op, lineNumber);
        } else if (type == 1 && op == Operation.label) {
            final String label = tag.getString("label");
            return new LabelLine(program, label, lineNumber);
        } else if (type == 2 && op == Operation.jmp) {
            final String label = tag.getString("label");
            return new JumpLine(program, label, lineNumber);
        }
        return null; // Should not happen!
    }
}

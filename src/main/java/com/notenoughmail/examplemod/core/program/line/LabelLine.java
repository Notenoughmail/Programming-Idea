package com.notenoughmail.examplemod.core.program.line;

import com.notenoughmail.examplemod.core.program.Operation;
import com.notenoughmail.examplemod.core.program.Program;
import net.minecraft.nbt.CompoundTag;

public class LabelLine extends Line {

    public LabelLine(Program program, String label, int lineNumber) {
        super(program, new Object[]{Operation.label, label}, Operation.label, lineNumber);
    }

    public String getLabel() {
        return (String) args[1];
    }

    @Override
    public void appendToProgram(StringBuilder builder) {
        builder.append(getLineNumber(lineNumber));
        builder.append("| ");
        builder.append(getLabel());
        builder.append(":\n");
    }

    @Override
    public void run() {} // Do nothing

    @Override
    public CompoundTag writeToNbt() {
        final CompoundTag tag = new CompoundTag();
        tag.putByte("type", (byte) 1);
        tag.putInt("operation", operation.ordinal());
        tag.putInt("lineNumber", lineNumber);
        tag.putString("label", getLabel());
        return tag;
    }
}

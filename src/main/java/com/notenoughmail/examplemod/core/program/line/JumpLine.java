package com.notenoughmail.examplemod.core.program.line;

import com.notenoughmail.examplemod.core.program.Operation;
import com.notenoughmail.examplemod.core.program.Program;
import net.minecraft.nbt.CompoundTag;

public class JumpLine extends Line {

    public JumpLine(Program program, String label, int lineNumber) {
        super(program, new Object[]{Operation.jmp, label}, Operation.jmp, lineNumber);
    }

    public String getLabel() {
        return (String) args[1];
    }

    @Override
    public CompoundTag writeToNbt() {
        final CompoundTag tag = new CompoundTag();
        tag.putByte("type", (byte) 2);
        tag.putInt("operation", operation.ordinal());
        tag.putInt("lineNumber", lineNumber);
        tag.putString("label", getLabel());
        return tag;
    }
}

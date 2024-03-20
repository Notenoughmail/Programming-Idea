package com.notenoughmail.examplemod.core.program;

import java.util.ArrayList;
import java.util.List;

public interface IProgramManager {

    List<Program> getPrograms();

    default List<String> getProgramNames() {
        final List<String> list = new ArrayList<>(getPrograms().size());
        getPrograms().forEach(program -> list.add(program.name));
        return list;
    }
}

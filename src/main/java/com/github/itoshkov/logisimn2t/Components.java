package com.github.itoshkov.logisimn2t;

import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Components extends Library {
    private final List<? extends Tool> tools = Arrays.asList(
            new AddTool(new ALU()),
            new AddTool(new PC())
    );

    @Override
    public List<? extends Tool> getTools() {
        return tools;
    }

    @Override
    public String getDisplayName() {
        return "ALU";
    }
}
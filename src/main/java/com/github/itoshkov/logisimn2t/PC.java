package com.github.itoshkov.logisimn2t;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstanceFactory;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.Port;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringUtil;
import java.awt.Color;
import java.awt.Graphics;

class PC extends InstanceFactory {
    private static final int PC_WIDTH = 160;
    private static final int PC_HEIGHT = 40;
    private static final int ULX = -160;
    private static final int ULY = -20;
    private static final int TOP_SEP = 40;
    private static final int LEFT_SEP = 10;
    private static final int IN = 0;
    private static final int IN_INC = 1;
    private static final int IN_LOAD = 2;
    private static final int IN_RESET = 3;
    private static final int OUT = 4;
    private static final int CK = 5;
    private static final int NUM_PORTS = 6;

    public PC() {
        super("PC");
        this.setOffsetBounds(Bounds.create(-160, -20, 160, 40));
        this.setAttributes(new Attribute[]{StdAttr.WIDTH, StdAttr.EDGE_TRIGGER}, new Object[]{BitWidth.create(16), StdAttr.TRIG_RISING});
        Port[] var1 = new Port[]{new Port(-160, -10, "input", StdAttr.WIDTH), new Port(-120, -20, "input", 1), new Port(-80, -20, "input", 1), new Port(-40, -20, "input", 1), new Port(0, 0, "output", StdAttr.WIDTH), new Port(-160, 10, "input", 1)};
        this.setPorts(var1);
    }

    public void propagate(InstanceState var1) {
        BitWidth var2 = (BitWidth)var1.getAttributeValue(StdAttr.WIDTH);
        PCData var3 = PCData.get(var1, var2);
        Object var4 = var1.getAttributeValue(StdAttr.EDGE_TRIGGER);
        boolean var5 = var3.updateClock(var1.getPortValue(5), var4);
        if (var5) {
            var3.setValue(nextValue(var1, var3.getValue()));
            var1.setPort(4, var3.getValue(), 9);
        }

    }

    public void paintInstance(InstancePainter var1) {
        Bounds var2 = var1.getBounds();
        BitWidth var3 = (BitWidth)var1.getAttributeValue(StdAttr.WIDTH);
        PCData var4 = PCData.get(var1, var3);
        GraphicsUtil.drawCenteredText(var1.getGraphics(), "PC:" + StringUtil.toHexString(var3.getWidth(), (int) var4.getValue().toIntValue()), var2.getX() + var2.getWidth() / 2, var2.getY() + 3 * var2.getHeight() / 4);
        var1.drawRectangle(var1.getBounds(), "");
        Graphics var5 = var1.getGraphics();
        var5.setColor(Color.BLACK);
        var1.drawClockSymbol(var2.getX(), var2.getY() + 30);
        var1.drawPort(5, "", Direction.EAST);
        var1.drawPort(0, "in", Direction.EAST);
        var1.drawPort(4, "out", Direction.WEST);
        var1.drawPort(1, "inc", Direction.NORTH);
        var1.drawPort(2, "load", Direction.NORTH);
        var1.drawPort(3, "reset", Direction.NORTH);
    }

    static Value nextValue(InstanceState var0, Value var1) {
        Value var2 = var0.getPortValue(4);
        BitWidth var3 = var2.getBitWidth();
        int var4;
        if (var0.getPortValue(3) == Value.TRUE) {
            var4 = 0;
        } else if (var0.getPortValue(2) == Value.TRUE) {
            Value var5 = var0.getPortValue(0);
            if (!var5.isFullyDefined()) {
                return var1;
            }

            var4 = var5.toIntValue();
        } else {
            if (var0.getPortValue(1) != Value.TRUE) {
                return var1;
            }

            var4 = var1.toIntValue() + 1;
        }

        return Value.createKnown(var3, var4);
    }
}
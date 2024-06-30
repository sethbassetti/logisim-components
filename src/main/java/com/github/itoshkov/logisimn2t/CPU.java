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
import java.awt.Color;
import java.awt.Graphics;

class CPU extends InstanceFactory {
    private static final int SCR_WIDTH = 290;
    private static final int SCR_HEIGHT = 180;
    private static final int ULX = -290;
    private static final int ULY = -90;
    private static final int LEFT_SEP = 10;
    private static final int P_INSTR = 0;
    private static final int P_IN = 1;
    private static final int P_RESET = 2;
    private static final int P_OUT = 3;
    private static final int P_ADDR = 4;
    private static final int P_WRITE = 5;
    private static final int P_PC = 6;
    private static final int CK = 7;
    private static final int NUM_PORTS = 8;

    public CPU() {
        super("CPU");
        this.setOffsetBounds(Bounds.create(-290, -90, 290, 180));
        this.setAttributes(new Attribute[]{StdAttr.WIDTH, StdAttr.EDGE_TRIGGER}, new Object[]{BitWidth.create(16), StdAttr.TRIG_RISING});
        Port[] var1 = new Port[]{new Port(-290, -30, "input", StdAttr.WIDTH), new Port(-290, 0, "input", StdAttr.WIDTH), new Port(-290, 30, "input", 1), new Port(0, -60, "output", StdAttr.WIDTH), new Port(0, 20, "output", 15), new Port(0, -20, "output", 1), new Port(0, 60, "output", 15), new Port(-290, -80, "input", 1)};
        this.setPorts(var1);
    }

    public void propagate(InstanceState var1) {
        BitWidth var2 = (BitWidth)var1.getAttributeValue(StdAttr.WIDTH);
        CPUData var3 = CPUData.get(var1, var2);
        Object var4 = var1.getAttributeValue(StdAttr.EDGE_TRIGGER);
        boolean var5 = var3.updateClock(var1.getPortValue(7), var4);
        Value var6 = var1.getPortValue(0);
        Value var7 = var1.getPortValue(1);
        Value var8 = var1.getPortValue(2);
        var3.set(var6, var7, var8, var5);
        var1.setPort(4, var3.getAddr(), 5);
        var1.setPort(6, var3.getRegPC(), 5);
        var1.setPort(3, var3.getOut(), 5);
        var1.setPort(5, var3.getWrite(), 1);
    }

    public void paintInstance(InstancePainter var1) {
        Bounds var2 = var1.getBounds();
        var1.drawRectangle(var1.getBounds(), "");
        Graphics var3 = var1.getGraphics();
        var3.setColor(Color.BLACK);
        var1.drawClockSymbol(var2.getX(), var2.getY() + 10);
        var1.drawPort(7, "", Direction.EAST);
        var1.drawPort(1, "in", Direction.EAST);
        var1.drawPort(0, "instr", Direction.EAST);
        var1.drawPort(2, "reset", Direction.EAST);
        var1.drawPort(3, "out", Direction.WEST);
        var1.drawPort(5, "write", Direction.WEST);
        var1.drawPort(4, "addr", Direction.WEST);
        var1.drawPort(6, "pc", Direction.WEST);
        BitWidth var4 = (BitWidth)var1.getAttributeValue(StdAttr.WIDTH);
        CPUData var5 = CPUData.get(var1, var4);
        Value var6 = var5.getRegA();
        Value var7 = var5.getRegD();
        Value var8 = var5.getRegPC();
        byte var9 = 45;
        GraphicsUtil.drawCenteredText(var1.getGraphics(), "A:" + var6.toHexString(), var2.getX() + 150, var2.getY() + 1 * var9);
        GraphicsUtil.drawCenteredText(var1.getGraphics(), "D:" + var7.toHexString(), var2.getX() + 150, var2.getY() + 2 * var9);
        GraphicsUtil.drawCenteredText(var1.getGraphics(), "PC:" + var8.toHexString(), var2.getX() + 150, var2.getY() + 3 * var9);
    }
}
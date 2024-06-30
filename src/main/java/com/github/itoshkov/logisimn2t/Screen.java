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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

class Screen extends InstanceFactory {
    private static final int SCR_WIDTH = 540;
    private static final int SCR_HEIGHT = 280;
    private static final int ULX = -540;
    private static final int ULY = -140;
    private static final int LEFT_SEP = 10;
    private static final int IN = 0;
    private static final int IN_ADDR = 1;
    private static final int IN_LOAD = 2;
    private static final int OUT = 3;
    private static final int CK = 4;
    private static final int NUM_PORTS = 5;

    public Screen() {
        super("Screen");
        this.setOffsetBounds(Bounds.create(-540, -140, 540, 280));
        this.setAttributes(new Attribute[]{StdAttr.WIDTH, StdAttr.EDGE_TRIGGER}, new Object[]{BitWidth.create(16), StdAttr.TRIG_RISING});
        Port[] var1 = new Port[]{new Port(-540, 0, "input", StdAttr.WIDTH), new Port(-540, 30, "input", 13), new Port(-540, 60, "input", 1), new Port(0, 0, "output", StdAttr.WIDTH), new Port(-540, -130, "input", 1)};
        this.setPorts(var1);
    }

    public void propagate(InstanceState var1) {
        BitWidth var2 = (BitWidth)var1.getAttributeValue(StdAttr.WIDTH);
        ScreenData var3 = ScreenData.get(var1, var2);
        Object var4 = var1.getAttributeValue(StdAttr.EDGE_TRIGGER);
        boolean var5 = var3.updateClock(var1.getPortValue(4), var4);
        Value var6 = var1.getPortValue(3);
        BitWidth var7 = var6.getBitWidth();
        Value var8 = var1.getPortValue(1);
        Value var9;
        if (!var8.isFullyDefined()) {
            var9 = Value.createKnown(var7, 0);
            var1.setPort(3, var9, 9);
        } else if (var5) {
            var9 = var3.getValue(var8);
            if (var9 == null) {
                var9 = Value.createKnown(var7, 0);
            }

            var1.setPort(3, var9, 9);
            if (var1.getPortValue(2) == Value.TRUE) {
                Value var10 = var1.getPortValue(0);
                if (!var10.isFullyDefined()) {
                    var10 = Value.createError(var7);
                }

                var3.setValue(var10, var8);
            }
        }

    }

    public void paintInstance(InstancePainter var1) {
        Bounds var2 = var1.getBounds();
        var1.drawRectangle(var1.getBounds(), "");
        Graphics var3 = var1.getGraphics();
        var3.setColor(Color.BLACK);
        var1.drawClockSymbol(var2.getX(), var2.getY() + 10);
        var1.drawPort(4, "", Direction.EAST);
        var1.drawPort(0, "I", Direction.EAST);
        var1.drawPort(3, "O", Direction.WEST);
        var1.drawPort(1, "A", Direction.EAST);
        var1.drawPort(2, "L", Direction.EAST);
        BitWidth var4 = (BitWidth)var1.getAttributeValue(StdAttr.WIDTH);
        ScreenData var5 = ScreenData.get(var1, var4);
        int var6 = (540 - var5.image.getWidth()) / 2;
        int var7 = (280 - var5.image.getHeight()) / 2;
        var3.drawImage(var5.image, var2.getX() + var6, var2.getY() + var7, var5.image.getWidth(), var5.image.getHeight(), (ImageObserver)null);
    }
}
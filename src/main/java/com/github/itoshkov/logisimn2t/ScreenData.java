package com.github.itoshkov.logisimn2t;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstanceData;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

class ScreenData implements InstanceData, Cloneable {
    private Value lastClock;
    public BufferedImage image;
    public Value[][] memory;

    public static ScreenData get(InstanceState var0, BitWidth var1) {
        ScreenData var2 = (ScreenData)var0.getData();
        if (var2 == null) {
            var2 = new ScreenData((Value)null);
            var0.setData(var2);
        }

        return var2;
    }

    public ScreenData(Value var1) {
        this.lastClock = var1;
        this.memory = new Value[256][32];
        this.image = new BufferedImage(512, 256, 12);
        Graphics var2 = this.image.getGraphics();
        var2.setColor(Color.WHITE);
        var2.clearRect(0, 0, this.image.getWidth(), this.image.getHeight());
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException var2) {
            return null;
        }
    }

    public boolean updateClock(Value var1, Object var2) {
        Value var3 = this.lastClock;
        this.lastClock = var1;
        if (var2 != null && var2 != StdAttr.TRIG_RISING) {
            if (var2 == StdAttr.TRIG_FALLING) {
                return var3 == Value.TRUE && var1 == Value.FALSE;
            } else if (var2 == StdAttr.TRIG_HIGH) {
                return var1 == Value.TRUE;
            } else if (var2 == StdAttr.TRIG_LOW) {
                return var1 == Value.FALSE;
            } else {
                return var3 == Value.FALSE && var1 == Value.TRUE;
            }
        } else {
            return var3 == Value.FALSE && var1 == Value.TRUE;
        }
    }

    public Value getValue(Value var1) {
        int var2 = this.memory[0].length;
        int var3 = var1.toIntValue();
        int var4 = var3 / var2;
        int var5 = var3 % var2;
        return this.memory[var4][var5];
    }

    public void setValue(Value var1, Value var2) {
        int var3 = this.memory[0].length;
        int var4 =  var2.toIntValue();
        int var5 = var4 / var3;
        int var6 = var4 % var3;
        this.memory[var5][var6] = var1;
        Graphics var7 = this.image.getGraphics();
        int var8 = var1.toIntValue();
        int var9 = 1;
        int var10 = var6 * 16;
        int var11 = var5;

        for(int var12 = 0; var12 < 16; ++var12) {
            if ((var9 & var8) != 0) {
                var7.setColor(Color.WHITE);
            } else {
                var7.setColor(Color.BLACK);
            }

            var7.drawLine(var10, var11, var10, var11);
            var9 <<= 1;
            ++var10;
        }

    }
}
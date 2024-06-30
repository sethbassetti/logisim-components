package com.github.itoshkov.logisimn2t;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstanceData;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;

class PCData implements InstanceData, Cloneable {
    private Value lastClock;
    private Value value;

    public static PCData get(InstanceState var0, BitWidth var1) {
        PCData var2 = (PCData)var0.getData();
        if (var2 == null) {
            var2 = new PCData((Value)null, Value.createKnown(var1, 0));
            var0.setData(var2);
        } else if (!var2.value.getBitWidth().equals(var1)) {
            var2.value = var2.value.extendWidth(var1.getWidth(), Value.FALSE);
        }

        return var2;
    }

    public PCData(Value var1, Value var2) {
        this.lastClock = var1;
        this.value = var2;
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

    public Value getValue() {
        return this.value;
    }

    public void setValue(Value var1) {
        this.value = var1;
    }
}
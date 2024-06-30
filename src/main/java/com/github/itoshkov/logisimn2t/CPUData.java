package com.github.itoshkov.logisimn2t;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstanceData;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;

class CPUData implements InstanceData, Cloneable {
    private Value lastClock;
    private Value regA;
    private Value regD;
    private Value regPC;
    private Value outM;
    private Value writeM;
    private Value addressM;

    public static CPUData get(InstanceState var0, BitWidth var1) {
        CPUData var2 = (CPUData)var0.getData();
        if (var2 == null) {
            var2 = new CPUData((Value)null);
            var0.setData(var2);
        }

        return var2;
    }

    public CPUData(Value var1) {
        this.lastClock = var1;
        this.regA = Value.createKnown(BitWidth.create(16), 0);
        this.regD = Value.createKnown(BitWidth.create(16), 0);
        this.regPC = Value.createKnown(BitWidth.create(15), 0);
        this.outM = Value.createKnown(BitWidth.create(16), 0);
        this.writeM = Value.createKnown(BitWidth.create(1), 0);
        this.addressM = Value.createKnown(BitWidth.create(15), 0);
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

    public Value getRegA() {
        return this.regA;
    }

    public Value getRegD() {
        return this.regD;
    }

    public Value getRegPC() {
        return this.regPC;
    }

    public Value getOut() {
        return this.outM;
    }

    public Value getWrite() {
        return this.writeM;
    }

    public Value getAddr() {
        return this.addressM;
    }

    public Value[] set(Value var1, Value var2, Value var3, boolean var4) {
        Value[] var5 = compute(var1, var2, var3, this.regA, this.regD, this.regPC, this.outM, var4);
        this.outM = var5[0];
        this.addressM = var5[1];
        this.writeM = var5[2];
        this.regPC = var5[3];
        this.regA = var5[4];
        this.regD = var5[5];
        return var5;
    }

    public static Value[] compute(Value var0, Value var1, Value var2, Value var3, Value var4, Value var5, Value var6, boolean var7) {
        boolean var8 = var0.get(15) == Value.TRUE;
        int var9 = (var0.get(5).toIntValue() << 2 | var0.get(4).toIntValue() << 1 | var0.get(3).toIntValue() << 0);
        boolean var11 = var9 == 4L || var9 == 5L || var9 == 6L || var9 == 7L;
        boolean var12 = var9 == 2L || var9 == 3L || var9 == 6L || var9 == 7L;
        boolean var13 = var9 == 1L || var9 == 3L || var9 == 5L || var9 == 7L;
        boolean var14 = !var8 || var11 && var8;
        boolean var15 = var12 && var8;
        Value var16 = var13 && var8 ? Value.TRUE : Value.FALSE;
        if (var7 && var14) {
            var3 = !var8 ? var0 : var6;
        }

        if (var7 && var15) {
            var4 = var6;
        }

        Value var20 = var0.get(12);
        Value var21 = var20 == Value.FALSE ? var3 : var1;
        Value var22 = var0.get(11);
        Value var23 = var0.get(10);
        Value var24 = var0.get(9);
        Value var25 = var0.get(8);
        Value var26 = var0.get(7);
        Value var27 = var0.get(6);
        Value[] var28 = {Value.createKnown(BitWidth.FIVE, 3)};
        var6 = var28[0];
        int var29 = (var0.get(2).toIntValue() << 2 | var0.get(1).toIntValue() << 1 | var0.get(0).toIntValue() << 0);
        boolean var38 = var28[1] == Value.TRUE;
        boolean var39 = var28[2] == Value.TRUE;
        boolean var40 = var39 || var38;
        boolean var41 = var8 && (var29 == 2L && var38 || var29 == 5L && !var38 || var29 == 4L && var39 || var29 == 6L && var40 || var29 == 1L && !var40 || var29 == 3L && !var39 || var29 == 7L);
        boolean var43 = !var41;
        if (var7) {
            if (var2 == Value.TRUE) {
                var5 = Value.createKnown(var5.getBitWidth(), 0);
            } else if (var41) {
                var5 = var3;
            } else if (var43) {
                int var44 = var5.toIntValue();
                var5 = Value.createKnown(var5.getBitWidth(), var44 + 1);
            }
        }

        Value[] var45 = new Value[]{var6, var3, var16, var5, var3, var4};
        return var45;
    }
}
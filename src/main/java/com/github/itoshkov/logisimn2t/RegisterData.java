

/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */
package com.github.itoshkov.logisimn2t;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstanceData;
import com.cburch.logisim.instance.InstanceState;

/** Represents the state of a counter. */
class RegisterData implements InstanceData, Cloneable {
    /** The last clock input value observed. */
    private Value lastClock;
    /** The current value emitted by the counter. */
    private Value dRegister;
    private Value aRegister;
    private Value pc;

    /** Constructs a state with the given values. */
    public RegisterData(Value lastClock, Value dRegister, Value aRegister, Value pc) {
        this.lastClock = lastClock;
        this.dRegister = dRegister;
        this.aRegister = aRegister;
        this.pc = pc;
    }

    /**
     * Retrieves the state associated with this counter in the circuit state, generating the state if
     * necessary.
     */
    public static RegisterData get(InstanceState state, BitWidth width) {
        RegisterData ret = (RegisterData) state.getData();
        if (ret == null) {
            // If it doesn't yet exist, then we'll set it up with our default
            // values and put it into the circuit state so it can be retrieved
            // in future propagations.
            ret = new RegisterData(null, Value.createKnown(width, 0), Value.createKnown(width, 0), Value.createKnown(width, 0));
            state.setData(ret);
        }
        return ret;
    }

    /** Returns a copy of this object. */
    @Override
    public Object clone() {
        // We can just use what super.clone() returns: The only instance
        // variables are
        // Value objects, which are immutable, so we don't care that both the
        // copy
        // and the copied refer to the same Value objects. If we had mutable
        // instance
        // variables, then of course we would need to clone them.
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /** Returns the current value emitted by the counter. */
    public Value getdRegister() {
        return dRegister;
    }

    /** Updates the current value emitted by the counter. */
    public void setdRegister(Value dRegister) {
        this.dRegister = dRegister;
    }

    public Value getaRegister(){
        return aRegister;
    }

    public void setaRegister(Value aRegister){
        this.aRegister = aRegister;
    }

    public Value getPc(){
        return pc;
    }

    public void setPc(Value pc){
        this.pc = pc;
    }

    /** Updates the last clock observed, returning true if triggered. */
    public boolean updateClock(Value value) {
        Value old = lastClock;
        lastClock = value;
        return old == Value.FALSE && value == Value.TRUE;
    }
}
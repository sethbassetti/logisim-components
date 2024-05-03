/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.github.itoshkov.logisimn2t;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstanceFactory;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.Port;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.util.GraphicsUtil;

import java.awt.*;

/**
 * This component takes a multibit input and outputs the value that follows it in Gray Code. For
 * instance, given input 0100 the output is 1100.
 */
class ALU extends InstanceFactory {
    /**
     * Unique identifier of the tool, used as reference in project files. Do NOT change as it will
     * prevent project files from loading.
     *
     * <p>Identifier value must MUST be unique string among all tools.
     */
    public static final String _ID = "ALU";

    /*
     * Note that there are no instance variables. There is only one instance of
     * this class created, which manages all instances of the component. Any
     * information associated with individual instances should be handled
     * through attributes. For GrayIncrementer, each instance has a "bit width"
     * that it works with, and so we'll have an attribute.
     */

    /** The constructor configures the factory. */
    ALU() {
        super(_ID);

        /*
         * This is how we can set up the attributes for GrayIncrementers. In
         * this case, there is just one attribute - the width - whose default is
         * 4. The StdAttr class defines several commonly occurring attributes,
         * including one for "bit width." It's best to use those StdAttr
         * attributes when appropriate: A user can then select several
         * components (even from differing factories) with the same attribute
         * and modify them all at once.
         */
        setAttributes(new Attribute[] {StdAttr.WIDTH}, new Object[] {BitWidth.create(16)});

        /*
         * The "offset bounds" is the location of the bounding rectangle
         * relative to the mouse location. Here, we're choosing the component to
         * be 100x100, and we're anchoring it relative to its primary output (as
         * is typical for Logisim), which happens to be in the center of the
         * east edge. Thus, the top left corner of the bounding box is 30 pixels
         * west and 15 pixels north of the mouse location.
         */
        setOffsetBounds(Bounds.create(-150, -40, 150, 80));

        /*
         * The ports are locations where wires can be connected to this
         * component. Each port object says where to find the port relative to
         * the component's anchor location, then whether the port is an
         * input/output/both, and finally the expected bit width for the port.
         * The bit width can be a constant (like 1) or an attribute (as here).
         */
        setPorts(
                new Port[] {
                        new Port(-150, -10, Port.INPUT, StdAttr.WIDTH),     // x
                        new Port(-150, 20, Port.INPUT, StdAttr.WIDTH),      // y
                        new Port(0, 0, Port.OUTPUT, StdAttr.WIDTH),         // out
                        new Port(-120, -40, Port.INPUT, 1),         // zx
                        new Port(-100, -40, Port.INPUT, 1),         // nx
                        new Port(-80, -40, Port.INPUT, 1),          // zy
                        new Port(-60, -40, Port.INPUT, 1),          // ny
                        new Port(-40, -40, Port.INPUT, 1),          // f
                        new Port(-20, -40, Port.INPUT, 1),          // no
                        new Port(-100, +40, Port.OUTPUT, 1),          // zr
                        new Port(-50, +40, Port.OUTPUT, 1),          // ng
                });
    }

    /**
     * Computes the next gray value in the sequence after prev. This static method just does some bit
     * twiddling; it doesn't have much to do with Logisim except that it manipulates Value and
     * BitWidth objects.
     */
    static Value nextValue(Value a, Value b) {
        final BitWidth bits = a.getBitWidth();
        if (!a.isFullyDefined() || !b.isFullyDefined()) return Value.createError(bits);
        var x = a.toLongValue();
        long y = b.toLongValue();
        long z = x + y;
        return Value.createKnown(bits, z);
    }

    /** Says how an individual instance should appear on the canvas. */
    @Override
    public void paintInstance(InstancePainter painter) {
        // As it happens, InstancePainter contains several convenience methods
        // for drawing, and we'll use those here. Frequently, you'd want to
        // retrieve its Graphics object (painter.getGraphics) so you can draw
        // directly onto the canvas.
        painter.drawBounds();


        final Bounds bds = painter.getBounds();
        Graphics g = painter.getGraphics();

        // Draw the ALU label
        int x0 = bds.getX() + 75;
        int y0 = bds.getY() + 40;

        g.setColor(Color.BLACK);
        GraphicsUtil.drawCenteredText(g, "ALU", x0, y0);

        GraphicsUtil.drawCenteredText(g, "zx", bds.getX() + 30, bds.getY() + 7);
        GraphicsUtil.drawCenteredText(g, "nx", bds.getX() + 50, bds.getY() + 7);
        GraphicsUtil.drawCenteredText(g, "zy", bds.getX() + 70, bds.getY() + 7);
        GraphicsUtil.drawCenteredText(g, "ny", bds.getX() + 90, bds.getY() + 7);
        GraphicsUtil.drawCenteredText(g, "f", bds.getX() + 110, bds.getY() + 7);
        GraphicsUtil.drawCenteredText(g, "no", bds.getX() + 130, bds.getY() + 7);

        GraphicsUtil.drawCenteredText(g, "out", bds.getX() + 135, bds.getY() + 37);

        GraphicsUtil.drawCenteredText(g, "x", bds.getX() + 7, bds.getY() + 27);
        GraphicsUtil.drawCenteredText(g, "y", bds.getX() + 7, bds.getY() + 55);

        GraphicsUtil.drawCenteredText(g, "zr", bds.getX() + 50, bds.getY() + bds.getHeight() - 12);
        GraphicsUtil.drawCenteredText(g, "ng", bds.getX() + 100, bds.getY() + + bds.getHeight() - 12);



        painter.drawPorts();
    }


    /**
     * Computes the current output for this component. This method is invoked any time any of the
     * inputs change their values; it may also be invoked in other circumstances, even if there is no
     * reason to expect it to change anything.
     */
    @Override
    public void propagate(InstanceState state) {
        // First we retrieve the value being fed into the input. Note that in
        // the setPorts invocation above, the component's input was included at
        // index 0 in the parameter array, so we use 0 as the parameter below.
        Value x = state.getPortValue(0);
        Value y = state.getPortValue(1);

        final BitWidth width = x.getBitWidth();

        final Value zx = state.getPortValue(3);
        final Value nx = state.getPortValue(4);
        final Value zy = state.getPortValue(5);
        final Value ny = state.getPortValue(6);
        final Value f = state.getPortValue(7);
        final Value no = state.getPortValue(8);

        Result result = getResult(x, y, zx, nx, zy, ny, f, no, width);
        state.setPort(2, result.out(), 0);
        state.setPort(9, Value.createKnown(1, result.zr()), 0);
        state.setPort(10, Value.createKnown(1, result.ng()), 0);

    }

    public static Result getResult(Value x, Value y, Value zx, Value nx, Value zy, Value ny, Value f, Value no, BitWidth width) {
        if (zx.toBinaryString().equals("1")){
            x = Value.createKnown(width, 0);
        }
        if (nx.toBinaryString().equals("1")){
            x = x.not();
        }
        if (zy.toBinaryString().equals("1")){
            y = Value.createKnown(width, 0);
        }
        if (ny.toBinaryString().equals("1")){
            y = y.not();
        }

        // Then check for the function we do, 0 = AND, 1 = Addition
        Value out;
        if (f.toBinaryString().equals("1")){
            out = Value.createKnown(width, x.toLongValue() + y.toLongValue());
        } else {
            out = x.and(y);
        }

        if (no.toBinaryString().equals("1")){
            out = out.not();
        }


        // Finally we propagate the output into the circuit. The first parameter
        // is 1 because in our list of ports (configured by invocation of
        // setPorts above) the output is at index 1. The second parameter is the
        // value we want to send on that port. And the last parameter is its
        // "delay" - the number of steps it will take for the output to update
        // after its input.


        long zr=0, ng=0;
        if (out.toLongValue() == 0) {
            zr = 1;
        }
        if (out.toBinaryString().charAt(0) == '1'){
            ng = 1;
        }
        Result result = new Result(out, zr, ng);
        return result;
    }

    public record Result(Value out, long zr, long ng) {
    }
}
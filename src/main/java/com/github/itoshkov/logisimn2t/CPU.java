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
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.InstanceFactory;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.Port;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringUtil;

/**
 * Manufactures a simple counter that iterates over the 4-bit Gray Code. This example illustrates
 * how a component can maintain its own internal state. All of the code relevant to state, though,
 * appears in CounterData class.
 */
class CPU extends InstanceFactory {
    /**
     * Unique identifier of the tool, used as reference in project files. Do NOT change as it will
     * prevent project files from loading.
     *
     * <p>Identifier value must MUST be unique string among all tools.
     */
    public static final String _ID = "CPU";

    private static final BitWidth BIT_WIDTH = BitWidth.create(16);

    // Again, notice how we don't have any instance variables related to an
    // individual instance's state. We can't put that here, because only one
    // SimpleGrayCounter object is ever created, and its job is to manage all
    // instances that appear in any circuits.

    public CPU() {
        super(_ID);
        setOffsetBounds(Bounds.create(0, 0, 300, 200));
        setPorts(
                new Port[] {
                        new Port(0, 10, Port.INPUT, 1),       // Clock - 0
                        new Port(300, 30, Port.OUTPUT, BIT_WIDTH.getWidth()),  // OutM - 1
                        new Port(0, 60, Port.INPUT, 16),             // Instruction - 2
                        new Port(0, 90, Port.INPUT, 16), // In Data - 3
                        new Port(0, 120, Port.INPUT, 1),  // Reset Button -4

                        new Port(300, 70, Port.OUTPUT, 1), // Write - 5
                        new Port(300, 110, Port.OUTPUT, 15), //Address - 6
                        new Port(300, 150, Port.OUTPUT, 15) // PC - 7
                });
    }

    @Override
    public void paintInstance(InstancePainter painter) {
        painter.drawBounds();
        painter.drawClock(0, Direction.EAST); // draw a triangle on port 0
        painter.drawPort(1); // draw port 1 as just a dot
        painter.drawPort(2);
        painter.drawPort(3);
        painter.drawPort(4);
        painter.drawPort(5);
        painter.drawPort(6);
        painter.drawPort(7);


        // Display the current counter value centered within the rectangle.
        // However, if the context says not to show state (as when generating
        // printer output), then skip this.
        if (painter.getShowState()) {
            final var state = RegisterData.get(painter, BIT_WIDTH);
            final var bds = painter.getBounds();
            GraphicsUtil.drawCenteredText(
                    painter.getGraphics(),
                    "A:" + StringUtil.toHexString(BIT_WIDTH.getWidth(), state.getaRegister().toLongValue()),
                    bds.getX() + bds.getWidth() / 2,
                    bds.getY() + bds.getHeight() / 4);

            GraphicsUtil.drawCenteredText(
                    painter.getGraphics(),
                    "D:" + StringUtil.toHexString(BIT_WIDTH.getWidth(), state.getdRegister().toLongValue()),
                    bds.getX() + bds.getWidth() / 2,
                    bds.getY() + bds.getHeight() / 2);

            GraphicsUtil.drawCenteredText(
                    painter.getGraphics(),
                    "PC:" + StringUtil.toHexString(BIT_WIDTH.getWidth(), state.getPc().toLongValue()),
                    bds.getX() + bds.getWidth() / 2,
                    bds.getY() + bds.getHeight() / 2 + bds.getHeight() / 4);
        }


    }

    private boolean[] getDest(String instruction){

        boolean aWrite, dWrite, mWrite;

        boolean isAInstruction = instruction.charAt(0) == '0';

        // Write to the A register on A instructions or when the 5th bit is high
        aWrite = isAInstruction || instruction.charAt(10) == '1';

        // Write to the D register when you have C instructions, and the 4th bit is high
        dWrite = !isAInstruction && instruction.charAt(11) == '1';

        // Write to memory on C instructions when the 3rd bit is high
        mWrite = !isAInstruction && instruction.charAt(12) == '1';

        return new boolean[]{aWrite, dWrite, mWrite};

    }

    private boolean getJump(String instruction, long zr, long ng){
        // If j1 is set (out < 0) and the output is negative
        boolean cond1 = instruction.charAt(13) == '1' && ng == 1;

        // If j2 is set (out = 0) and the output is 0
        boolean cond2 = instruction.charAt(14) == '1' && zr == 1;

        // If j3 is set (out > 0) and the output is neither negative nor 0
        boolean cond3 = instruction.charAt(15) == '1' && zr == 0 && ng == 0;

        boolean jump = (cond1 || cond2 || cond3) && instruction.charAt(0) == '1';

        return jump;
    }

    @Override
    public void propagate(InstanceState state) {
        // Grab the current instruction
        final String instruction = state.getPortValue(2).toBinaryString();

        // First, determine the write-flags for A Register, D register and Memory
        boolean aWrite, dWrite, mWrite;
        boolean[] destValues = getDest(instruction);
        aWrite = destValues[0];
        dWrite = destValues[1];
        mWrite = destValues[2];

        // Retrieve the current values of the registers
        final var registers = RegisterData.get(state, BIT_WIDTH);

        Value dRegister = registers.getdRegister();
        Value aRegister = registers.getaRegister();
        Value pc = registers.getPc();

        // Determine what to use as the operands to the ALU
        Value x, y;
        x = dRegister;

        // y will either be inM or aRegister
        if (instruction.charAt(3) == '1'){
            y = state.getPortValue(3);
        } else {
            y = aRegister;
        }

        // Extract the control bits for the ALU
        Value zx = Value.createKnown(1, instruction.charAt(4) - '0');
        Value nx = Value.createKnown(1, instruction.charAt(5) - '0');
        Value zy = Value.createKnown(1, instruction.charAt(6) - '0');
        Value ny = Value.createKnown(1, instruction.charAt(7) - '0');
        Value f = Value.createKnown(1, instruction.charAt(8) - '0');
        Value no = Value.createKnown(1, instruction.charAt(9) - '0');

        // Send all values into the ALU and extract the results
        ALU.Result result = ALU.getResult(x, y, zx, nx, zy, ny, f, no, BIT_WIDTH);

        Value ALUout = result.out();
        long zr = result.zr();
        long ng = result.ng();

        // Use the ALU Output to determine if we jump
        boolean jump = getJump(instruction, zr, ng);

        // First, set all of our output port values
        state.setPort(1, ALUout, 0); // outM
        state.setPort(5, Value.createKnown(1, mWrite ? 1L : 0L), 0); //writeM
        state.setPort(6, Value.createKnown(15, aRegister.toLongValue()), 0);



        // Finally, check to see if we are on a rising clock edge and update our registers
        final var trigger = registers.updateClock(state.getPortValue(0));

        if (trigger){
            if (aWrite){
                // If we have a C instruction, send ALU output into aRegister, otherwise instruction
                if (instruction.charAt(0) == '1'){
                    registers.setaRegister(ALUout);
                } else {
                    registers.setaRegister(state.getPortValue(2));
                }
            }

            if (dWrite){
                registers.setdRegister(ALUout);
            }

            // Check to see how we update the PC
            // If we reset it, set it to 0
            if(jump){
                registers.setPc(aRegister);
            } else {
                registers.setPc(Value.createKnown(16, pc.toLongValue() + 1));
           }
        }

        // Check to see if we reset (regardless of clock state)
        if (state.getPortValue(4).toLongValue() == 1){
            registers.setPc(Value.createKnown(16, 0));
        }
        state.setPort(7, Value.createKnown(15, registers.getPc().toLongValue()), 0);

    }
}
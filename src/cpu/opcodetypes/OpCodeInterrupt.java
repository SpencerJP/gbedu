package cpu.opcodetypes;

import cpu.GameBoyCPU;
import cpu.opcodetypes.enums.InterruptCommands;
import main.Util;
import mmu.GameBoyMMU;

public class OpCodeInterrupt extends OpCode {


    private InterruptCommands command;

    public OpCodeInterrupt(String doc, int cycles, int instructionSize, InterruptCommands command) {
        super(doc, cycles, instructionSize);
        this.command = command;
    }

    @Override
    public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
        switch(command) {
            case ENABLE_INTERRUPTS:
                Util.getCPU().setDelayedInterruptsEnabled(true);
                break;
            case DISABLE_INTERRUPTS:
                Util.getCPU().setInterruptsEnabled(false);
                break;
            default:
                throw new UnsupportedOperationException("OpCodeInterrupt.runCode: This shouldn't happen.");
        }
        return cycles;
    }
}

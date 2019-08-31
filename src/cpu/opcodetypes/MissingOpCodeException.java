package cpu.opcodetypes;

import main.Util;
import mmu.GameBoyMMU;

public class MissingOpCodeException extends Exception {

    public String opCode;
    public String opCodePosition;
    public int programCounter;
    public MissingOpCodeException(GameBoyMMU mmu, int programCounter) {
        opCode = Util.byteToHex(mmu.getMemoryAtAddress(programCounter));
        if (opCode.equals("cb")) {
            int nextCode = Util.getMemory().getMemoryAtAddress(programCounter + 1);
            opCode = opCode + Util.byteToHex(nextCode);
        }
        this.programCounter = programCounter;
        opCodePosition = Util.byteToHex(programCounter);
    }

    @Override
    public String getMessage() {
        return "Opcode " + opCode + " at program position " + programCounter +" (0x"+opCodePosition+") is either invalid or has not been implemented";
    }
}

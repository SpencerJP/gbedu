package opcodetypes;

import mmu.GameBoyMMU;
import cpu.GameBoyCPU;
import cpu.opcodetypes.OpCode;

public class OpCodeNOP extends OpCode {

	public OpCodeNOP(int cycles, int instructionSize, int programAddress) {
		super(cycles, instructionSize, programAddress);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		return cycles;
	}

}

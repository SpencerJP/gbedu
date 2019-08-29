package cpu.opcodetypes;

import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeSTOP extends OpCode {

	public OpCodeSTOP(int programAddress) {
		super(4, 2, programAddress);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) {
		return cycles;
	}

}

package cpu.opcodetypes;

import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeSTOP extends OpCode {

	public OpCodeSTOP() {
		super(4, 2);
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) {
		//TODO this like ends the cpu
		return cycles;
	}

}

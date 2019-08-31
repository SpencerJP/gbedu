package cpu.opcodetypes;

import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeNOP extends OpCode {

	public OpCodeNOP() {
		super("NOP", 4, 1);
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		return cycles;
	}

}

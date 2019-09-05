package cpu.opcodetypes;

import cpu.GameBoyCPU;
import mmu.GameBoyMMU;

public class OpCodeNOP extends OpCode {

	public OpCodeNOP() {
		super("NOP", 4, 1);
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		return cycles;
	}

}

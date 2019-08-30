package cpu.opcodetypes;

import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeJump extends OpCode {
	
	private OpCodeCondition condition;
	private int destAddress;
	private int destAddress2;

	public OpCodeJump(int cycles, int instructionSize, OpCodeCondition condition) {
		super(cycles, instructionSize);
		this.condition = condition;
	}


	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		destAddress = getRelativeMemory(2);
		destAddress2 = getRelativeMemory(1);
		destAddress = (destAddress << 8) | destAddress2;
		if(condition != null) {
			switch(condition) {
			case Z:
				if(getFlagZ()) {
					break;
				}
				return cycles;
			case NZ:
				if(!getFlagZ()) {
					break;
				}
				return cycles;
			case C:
				if(getFlagC()) {
					break;
				}
				return cycles;
			case NC:
				if(!getFlagC()) {
					break;
				}
				return cycles;
				
			}

			cpu.programCounter = destAddress;
			return cycles;
		}
		cpu.programCounter = destAddress;
		return cycles;
	}

}

package cpu.opcodetypes;

import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeJump extends OpCode {
	
	private OpCodeCondition condition;
	private int destAddress;

	public OpCodeJump(int cycles, int instructionSize, int programAddress, int address, OpCodeCondition condition) {
		super(cycles, instructionSize, programAddress);
		destAddress = address;
		this.condition = condition;
	}
	
	public OpCodeJump(int cycles, int instructionSize, int programAddress, int address) {
		super(cycles, instructionSize, programAddress);
		destAddress = address;
		this.condition = condition;
	}


	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
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

package cpu.opcodetypes;

import cpu.GameBoyCPU;
import main.Utility;
import mmu.GameBoyMMU;

public class OpCodeLD extends OpCode {
	public static final int INCREMENT_ADDRESS = 1;
	public static final int DECREMENT_ADDRESS = 2;
	private int destAddress = -1;
	private int source = 0;
	private int source2 = 0;
	private OpCodeRegister register;
	private OpCodeRegister sourceRegister = null;
	
	public OpCodeLD(int cycles, int instructionSize,
			int destAddress, int source) {
		super(cycles, instructionSize);
		this.destAddress = destAddress;
		this.source = source;
	}

	public OpCodeLD(int cycles, int instructionSize, OpCodeRegister register) {
		super(cycles, instructionSize);
		this.register = register;
		switch(register) {
		case REGISTERS_SP:
		case REGISTERS_HL:
		case REGISTERS_BC:
		case REGISTERS_DE:
			source = getRelativeMemory(2);
			source2 = getRelativeMemory(1);
			source = (source << 8) | source2;
			break;
		default:
			source = getRelativeMemory(1);
			break;
		}
	}

	public OpCodeLD(int cycles, int instructionSize,
			OpCodeRegister register, OpCodeRegister sourceRegister) {
		super(cycles, instructionSize);
		this.register = register;
		this.sourceRegister = sourceRegister;
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		if (destAddress != -1) {
			mmu.setMemoryAtAddress(destAddress, source);
		}
		else if(sourceRegister != null) {
			setRegister(cpu, register, getRegister(cpu, sourceRegister));
		}
		else {
			setRegister(cpu, register, source);
		}
		return cycles;
	}

}

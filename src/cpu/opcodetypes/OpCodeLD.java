package cpu.opcodetypes;

import cpu.GameBoyCPU;
import mmu.GameBoyMMU;

public class OpCodeLD extends OpCode {
	public static final int INCREMENT_ADDRESS = 1;
	public static final int DECREMENT_ADDRESS = 2;
	private int destAddress = -1;
	private int source = 0;
	private int source2 = 0;
	private OpCodeRegister register;
	
	public OpCodeLD(int cycles, int instructionSize, int programAddress,
			int destAddress, int source) {
		super(cycles, instructionSize, programAddress);
		this.destAddress = destAddress;
		this.source = source;
	}

	public OpCodeLD(int cycles, int instructionSize, int programAddress,OpCodeRegister register) {
		super(cycles, instructionSize, programAddress);
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

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		if (destAddress != -1) {
			mmu.setMemoryAtAddress(destAddress, source);
		}
		else {
			setRegister(cpu, register, source);
		}
		return cycles;
	}

}

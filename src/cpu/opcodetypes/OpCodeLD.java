package cpu.opcodetypes;

import cpu.GameBoyCPU;
import cpu.opcodetypes.enums.OpCodeRegister;
import mmu.GameBoyMMU;

public class OpCodeLD extends OpCode {
	public static final int INCREMENT_ADDRESS = 1;
	public static final int DECREMENT_ADDRESS = 2;
	private int destAddress = -1;
	private int source = 0;
	private int source2 = 0;
	private OpCodeRegister register;
	private OpCodeRegister sourceRegister = null;

	public OpCodeLD(String doc, int cycles, int instructionSize, OpCodeRegister register) {
		super(doc, cycles, instructionSize);
		this.register = register;
	}

	public OpCodeLD(String doc, int cycles, int instructionSize, OpCodeRegister register, int destAddress) {
		super(doc, cycles, instructionSize);
		this.register = register;
		this.destAddress = destAddress;
	}

	public OpCodeLD(String doc, int cycles, int instructionSize,
			OpCodeRegister register, OpCodeRegister sourceRegister) {
		super(doc, cycles, instructionSize);
		this.register = register;
		this.sourceRegister = sourceRegister;
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		switch(register) {
			case REGISTERS_SP:
			case REGISTERS_HL:
			case REGISTERS_BC:
			case REGISTERS_DE:
			case A_TO_ADDRESS:
			case ADDRESS_TO_A:
				source = getOperand16bit(cpu);
				break;
			default:
				source = getOperand8bit(cpu);
				break;
		}
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

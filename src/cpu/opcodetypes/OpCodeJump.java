package cpu.opcodetypes;

import cpu.GameBoyCPU;
import cpu.opcodetypes.enums.JumpType;
import cpu.opcodetypes.enums.OpCodeCondition;
import cpu.opcodetypes.enums.OpCodeRegister;
import main.Util;
import mmu.GameBoyMMU;

public class OpCodeJump extends OpCode {

	private OpCodeRegister register;
	private OpCodeCondition condition;
	private int destAddress;
	private JumpType jumpType;
	private int source;

	public OpCodeJump(String doc, int instructionSize, JumpType type, OpCodeCondition condition) {
		super(doc, -1, instructionSize);
		jumpType = type;
		this.condition = condition;
	}
	public OpCodeJump(String doc, int instructionSize, JumpType type) {
		super(doc, -1, instructionSize);
		jumpType = type;
	}
	// for restarts
	public OpCodeJump(String doc, int instructionSize, JumpType type, int source) {
		super(doc, -1, instructionSize);
		jumpType = type;
		this.source = source;
	}

	public OpCodeJump(String doc, int instructionSize, JumpType type, OpCodeRegister register) {
		super(doc, -1, instructionSize);
		jumpType = type;
		this.register = register;



	}


	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {

		switch(jumpType) {
			case CALL:
			case JUMP_TO_ADDRESS:
				if(register != null) {
					destAddress = getRegister(cpu, register);
				}
				else {
					destAddress = getOperand16bit(cpu);
				}
				break;
			case ADD_TO_ADDRESS:
				byte jumpLength = (byte) getOperand8bit(cpu); // this value is a signed byte
				destAddress = Util.getCPU().getProgramCounter() + jumpLength;
				break;
			case RETURN:
			case RETI:
				destAddress = cpu.popSP();
				break;
			case RESTART:
				destAddress = source;
				break;
			default:
				throw new Exception("OpCodeJump mistake");
		}
		if(condition != null) {
			switch(condition) {
				case Z:
					if(getFlagZ()) {
						break;
					}
					return 8;
				case NZ:
					if(!getFlagZ()) {
						break;
					}
					return 8;
				case C:
					if(getFlagC()) {
						break;
					}
					return 8;
				case NC:
					if(!getFlagC()) {
						break;
					}
					return 8;

			}
			if (jumpType == JumpType.CALL) {
				Util.getCPU().pushSP(Util.getCPU().getProgramCounter());
				cpu.setProgramCounter(destAddress);
				return 24;
			}
			if (jumpType == JumpType.RETURN) {
				cpu.setProgramCounter(destAddress);
				return 16;
			}
			cpu.setProgramCounter(destAddress);
			return 12;
		}
		if (jumpType == JumpType.CALL) {
			Util.getCPU().pushSP(Util.getCPU().getProgramCounter());
			cpu.setProgramCounter(destAddress);
			return 24;
		}
		if (jumpType == JumpType.RETURN) {
			cpu.setProgramCounter(destAddress);
			return 16;
		}
		
		if (jumpType == JumpType.RETI) {
			Util.getCPU().setDelayedInterruptsEnabled(true);
			cpu.setProgramCounter(destAddress);
			return 16;
		}
		if (jumpType == JumpType.RESTART) {
			Util.getCPU().pushSP(Util.getCPU().getProgramCounter());
			cpu.setProgramCounter(destAddress);
			return 32;
		}
		cpu.setProgramCounter(destAddress);
		return 12;
	}

}

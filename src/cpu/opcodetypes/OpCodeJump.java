package cpu.opcodetypes;

import cpu.GameBoyCPU;
import cpu.opcodetypes.enums.JumpType;
import cpu.opcodetypes.enums.OpCodeCondition;
import main.Util;
import mmu.GameBoyMMU;

public class OpCodeJump extends OpCode {
	
	private OpCodeCondition condition;
	private int destAddress;
	private JumpType jumpType;

	public OpCodeJump(String doc, int instructionSize, JumpType type, OpCodeCondition condition) {
		super(doc, -1, instructionSize);
		jumpType = type;
		this.condition = condition;
	}
	public OpCodeJump(String doc, int instructionSize, JumpType type) {
		super(doc, -1, instructionSize);
		jumpType = type;
	}


	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {

		if (jumpType == JumpType.JUMP_TO_ADDRESS) {
			destAddress = getOperand16bit(cpu);
		} else if(jumpType == JumpType.ADD_TO_ADDRESS) {
			byte jumpLength = (byte) getOperand8bit(cpu);
			destAddress = Util.getCPU().getProgramCounter() + jumpLength; // this value is a signed byte
		} else if(jumpType == JumpType.CALL) {
			destAddress = getOperand16bit(cpu);

		} else if(jumpType == JumpType.RETURN) {
			destAddress = cpu.popSP();
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
		cpu.setProgramCounter(destAddress);
		return 12;
	}

}

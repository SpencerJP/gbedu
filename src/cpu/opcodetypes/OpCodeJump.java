package cpu.opcodetypes;

import cpu.GameBoyCPU;
import cpu.opcodetypes.enums.JumpType;
import cpu.opcodetypes.enums.OpCodeCondition;
import main.Util;
import mmu.GameBoyMMU;

public class OpCodeJump extends OpCode {
	
	private OpCodeCondition condition;
	private int destAddress;
	private int destAddress2;
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
			destAddress = getRelativeMemory(cpu, 2);
			destAddress2 = getRelativeMemory(cpu, 1);
			destAddress = (destAddress << 8) | destAddress2;
		} else if(jumpType == JumpType.ADD_TO_ADDRESS) {
			byte jumpLength = (byte) getRelativeMemory(cpu, 1);
			destAddress = Util.getCPU().getProgramCounter() + jumpLength; // this value is a signed byte
		} else if(jumpType == JumpType.CALL) {
			destAddress = getRelativeMemory(cpu, 2);
			destAddress2 = getRelativeMemory(cpu, 1);
			destAddress = (destAddress << 8) | destAddress2;
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
				Util.getCPU().pushSP(Util.getCPU().getProgramCounter() + getInstructionSize());
				cpu.setProgramCounter(destAddress);
				cpu.isJumping = true;
				return 24;
			}
			if (jumpType == JumpType.RETURN) {
				cpu.setProgramCounter(destAddress);
				cpu.isJumping = true;
				return 16;
			}
			cpu.setProgramCounter(destAddress);
			return 12;
		}
		if (jumpType == JumpType.CALL) {
			Util.getCPU().pushSP(Util.getCPU().getProgramCounter() + getInstructionSize());
			cpu.setProgramCounter(destAddress);
			cpu.isJumping = true;
			return 24;
		}
		if (jumpType == JumpType.RETURN) {
			cpu.setProgramCounter(destAddress);
			cpu.isJumping = true;
			return 16;
		}
		cpu.setProgramCounter(destAddress);
		return 12;
	}

}

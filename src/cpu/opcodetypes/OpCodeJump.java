package cpu.opcodetypes;

import cpu.opcodetypes.enums.JumpType;
import cpu.opcodetypes.enums.OpCodeCondition;
import main.Util;
import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeJump extends OpCode {
	
	private OpCodeCondition condition;
	private int destAddress;
	private int destAddress2;
	private JumpType jumpType;

	public OpCodeJump(int instructionSize, JumpType type, OpCodeCondition condition) {
		super(-1, instructionSize);
		jumpType = type;
		this.condition = condition;
	}
	public OpCodeJump(int instructionSize, JumpType type) {
		super(-1, instructionSize);
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
			System.out.println(jumpLength);
			destAddress = Util.getCPU().getProgramCounter() + jumpLength; // this value is a signed byte
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

			cpu.setProgramCounter(destAddress);
			//cpu.isJumping = true;
			return 12;
		}
		cpu.setProgramCounter(destAddress);
		//cpu.isJumping = true;
		return 12;
	}

}

package cpu.opcodetypes;

import cpu.opcodetypes.enums.OpCodeFunction;
import cpu.opcodetypes.enums.OpCodeRegister;
import main.Util;
import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeBit extends OpCode {
	
	private OpCodeFunction function;
	private int bitPosition;
	private OpCodeRegister register;

	public OpCodeBit(int cycles, int instructionSize, OpCodeFunction function, OpCodeRegister register, int position ) {
		super(cycles, instructionSize);
		this.function = function;
		this.bitPosition = position;
		this.register = register;
		
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		switch(function) {
		case BIT:
			setFlagZ(!getBitFromRegister(register, bitPosition));
			setFlagN(false);
			setFlagH(true);
			break;
		default:
			throw new UnsupportedOperationException("Not implemented");
		}
		return 0;
	}

}

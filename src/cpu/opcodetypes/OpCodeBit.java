package cpu.opcodetypes;

import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeBit extends OpCode {
	
	private OpCodeFunction function;
	private int bitPosition;
	private OpCodeRegister register;

	public OpCodeBit(int cycles, int instructionSize, OpCodeFunction function, int position, OpCodeRegister register ) {
		super(cycles, instructionSize);
		this.function = function;
		this.bitPosition = position;
		this.register = register;
		
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		switch(function) {
		case BIT:
			setFlagZ(getBitFromRegister(register, bitPosition));
			setFlagN(false);
			setFlagH(true);
			break;
		default:
			throw new UnsupportedOperationException("Not implemented");
		}
		return 0;
	}

}

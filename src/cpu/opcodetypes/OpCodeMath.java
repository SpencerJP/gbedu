package cpu.opcodetypes;

import cpu.opcodetypes.enums.OpCodeFunction;
import cpu.opcodetypes.enums.OpCodeRegister;
import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeMath extends OpCode {
	
	private OpCodeFunction function;
	private int source;
	private OpCodeRegister register;

	public OpCodeMath(int cycles, int instructionSize, OpCodeFunction function, OpCodeRegister register, byte data) {
		super(cycles, instructionSize);
	}
	
	public OpCodeMath(int cycles, int instructionSize, OpCodeFunction function, OpCodeRegister register) {
		super(cycles, instructionSize);
		this.function = function;
		this.register = register;
	}

    @Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		switch(function) {
		case XOR:
			setAccumulator(cpu, getRegister(cpu, register)^getAccumulator());
			setFlags(false, false, false, (getRegister(cpu,register) == 0));
			break;
		case AND:
			setAccumulator(cpu, getRegister(cpu, register) & getAccumulator());
			setFlags(false, false, false, (getRegister(cpu,register) == 0));
			break;
		case OR:
			setAccumulator(cpu, getRegister(cpu, register) | getAccumulator());
			setFlags(false, false, false, (getRegister(cpu,register) == 0));
			break;
		case ADD:
			boolean hFlag = (((getAccumulator() & 0xf) + (getRegister(cpu, register) & 0xf)) & 0x10) == 0x10;
			int result = getAccumulator() + getRegister(cpu, register);
			boolean cFlag = result > 255;
			setAccumulator(cpu, result & 0xFF);
			setFlagZ(getAccumulator() == 0x00);
			setFlagH(hFlag);
			setFlagC(cFlag);
			break;
		default:
			throw new UnsupportedOperationException("function not implemented");
		}
		return cycles;
	}

	
	

}

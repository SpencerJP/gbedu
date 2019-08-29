package cpu.opcodetypes;

import main.Utility;
import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeMath extends OpCode {
	
	private OpCodeFunction function;
	private int source;
	private OpCodeRegister register;

	public OpCodeMath(int cycles, int instructionSize, int programAddress, OpCodeFunction function, OpCodeRegister register, byte data) {
		super(cycles, instructionSize, programAddress);
		// TODO Auto-generated constructor stub
	}
	
	public OpCodeMath(int cycles, int instructionSize, int programAddress, OpCodeFunction function, OpCodeRegister register) {
		super(cycles, instructionSize, programAddress);
		this.function = function;
		this.register = register;
		switch(function) {
		case XOR:
			source = Utility.getCPU().getA();
			break;
		default:
			break;
		}
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		switch(function) {
		case XOR:
			setRegister(cpu, register, getRegister(cpu, register)^getAccumulator());
			setFlags(false, false, false, (getRegister(cpu,register) == 0));
			break;
		default:
			throw new UnsupportedOperationException("function not implemented");
		}
		return cycles;
	}
	
	

}

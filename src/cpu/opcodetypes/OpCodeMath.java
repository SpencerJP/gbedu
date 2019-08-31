package cpu.opcodetypes;

import cpu.opcodetypes.enums.OpCodeFunction;
import cpu.opcodetypes.enums.OpCodeRegister;
import main.Util;
import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

public class OpCodeMath extends OpCode {
	
	private OpCodeFunction function;
	private int source;
	private OpCodeRegister register;
	
	public OpCodeMath(String doc,int cycles, int instructionSize, OpCodeFunction function, OpCodeRegister register) {
		super(doc, cycles, instructionSize);
		this.function = function;
		this.register = register;
	}

	public OpCodeMath(String doc,int cycles, int instructionSize, OpCodeFunction function) {
		super(doc, cycles, instructionSize);
		this.function = function;
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		boolean hFlag;
		boolean cFlag;
		int result;
		int accum;
		switch(function) {
			case XOR:
				if (register == null) {
					source = getRelativeMemory(cpu, 1);
				}
				else {
					source = getRegister(cpu, register);
				}
				setAccumulator(cpu, source^getAccumulator());
				setFlags(false, false, false, (getRegister(cpu,register) == 0));
				break;
			case AND:
				if (register == null) {
					source = getRelativeMemory(cpu, 1);
				}
				else {
					source = getRegister(cpu, register);
				}
				setAccumulator(cpu, source & getAccumulator());
				setFlags(false, true, false, (getRegister(cpu,register) == 0));
				break;
			case OR:
				if (register == null) {
					source = getRelativeMemory(cpu, 1);
				}
				else {
					source = getRegister(cpu, register);
				}
				setAccumulator(cpu, source | getAccumulator());
				setFlags(false, false, false, (getRegister(cpu,register) == 0));
				break;
			case ADD:
				hFlag = (((getAccumulator() & 0xf) + (getRegister(cpu, register) & 0xf)) & 0x10) == 0x10;
				result = getAccumulator() + getRegister(cpu, register);
				cFlag = result > 255;
				setAccumulator(cpu, result & 0xFF);
				setFlagZ(getAccumulator() == 0x00);
				setFlagH(hFlag);
				setFlagC(cFlag);
				break;
			case PUSH:
				cpu.pushSP(getRegister(cpu, register));
				break;
			case POP:
				setRegister(cpu, register, cpu.popSP());
				break;
			case INC:
				result = (getRegister(cpu, register) + 1) & 0xff;
				setRegister(cpu, register, result);
				setFlagZ(result == 0);
				setFlagN(false);
				setFlagH((getRegister(cpu, register) & 0x0f) == 0x0f);
				break;
			case DEC:
				result = (getRegister(cpu, register) - 1) & 0xff;
				setRegister(cpu, register, result);
				setFlagZ(result == 0);
				setFlagN(false);
				setFlagH((getRegister(cpu, register) & 0x0f) == 0x0);
				break;
			case INC_16:
				setRegister(cpu, register, (getRegister(cpu, register) + 1) & 0xffff);
				break;
			case DEC_16:
				setRegister(cpu, register, (getRegister(cpu, register) - 1) & 0xffff);
				break;
			case CP:
				if (register == null) {
					source = getRelativeMemory(cpu, 1);
				}
				accum = getAccumulator();
				System.out.println("accum: " + accum + ", source:" + source);
				System.out.println(((accum - source) & 0xff) == 0);
				setFlagZ(((accum - source) & 0xff) == 0);
				setFlagN(true);
				setFlagH((0x0f & source) > (0x0f & accum));
				setFlagC(source > accum);
				break;
			default:
				throw new UnsupportedOperationException("Math function not implemented");
			}
		return cycles;
	}

	
	

}

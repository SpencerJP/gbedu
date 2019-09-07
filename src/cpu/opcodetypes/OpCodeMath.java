package cpu.opcodetypes;

import cpu.GameBoyCPU;
import cpu.opcodetypes.enums.OpCodeFunction;
import cpu.opcodetypes.enums.OpCodeRegister;
import mmu.GameBoyMMU;

public class OpCodeMath extends OpCode {

	private OpCodeFunction function;
	private int source;
	private OpCodeRegister register;
	private OpCodeRegister register2;

	public OpCodeMath(String doc,int cycles, int instructionSize, OpCodeFunction function, OpCodeRegister register) {
		super(doc, cycles, instructionSize);
		this.function = function;
		this.register = register;
	}

	public OpCodeMath(String doc,int cycles, int instructionSize, OpCodeFunction function, OpCodeRegister register, OpCodeRegister register2) {
		super(doc, cycles, instructionSize);
		this.function = function;
		this.register = register;
		this.register2 = register2;
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
		if (register == null) {
			source = getOperand8bit(cpu);
		}
		else {
			source = getRegister(cpu, register);
		}
		switch(function) {
			case XOR:
				setAccumulator(cpu, source^getAccumulator());
				setFlags(false, false, false, (getAccumulator() == 0));
				break;
			case AND:
				setAccumulator(cpu, source & getAccumulator());
				setFlags(false, true, false, (getAccumulator() == 0));
				break;
			case OR:
				setAccumulator(cpu, source | getAccumulator());
				setFlags(false, false, false, (getAccumulator() == 0));
				break;
			case ADD:
				hFlag = (((getRegister(cpu, register) & 0xf) + (getRegister(cpu, register2) & 0xf)) & 0x10) == 0x10;
				result = getRegister(cpu, register) + getRegister(cpu, register2);
				cFlag = result > 255;
				setRegister(cpu, register, result);
				setFlagZ(getRegister(cpu, register) == 0x00);
				setFlagH(hFlag);
				setFlagC(cFlag);
				break;
			case SUB:
				accum = getAccumulator();
				setFlagZ(((accum - source) & 0xff) == 0);
				setFlagN(true);
				setFlagH((0x0f & source) > (0x0f & accum));
				setFlagC(source > accum);
				setAccumulator(cpu, ((accum - source) & 0xff));
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
				accum = getAccumulator();
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

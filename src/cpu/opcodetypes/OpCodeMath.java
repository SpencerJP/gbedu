package cpu.opcodetypes;

import cpu.GameBoyCPU;
import cpu.opcodetypes.enums.OpCodeFunction;
import cpu.opcodetypes.enums.OpCodeRegister;
import mmu.GameBoyMMU;

public class OpCodeMath extends OpCode {

	private OpCodeFunction function;
	private int source;
	private int source2;
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
		int carry;
		if (register == null) {
			source = getOperand8bit(cpu);
		}
		else {
			source = getRegister(cpu, register);
		}
		if(register2 == null) {
			source2 = getAccumulator();
		}
		else {
			source2 = getRegister(cpu, register2);
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
				result = source + source2;
				setAccumulator(cpu, result);
				setFlagZ((result & 0xff) == 0x00);
				setFlagN(false);
				setFlagH((((source & 0xf) + (source2 & 0xf)) & 0x10) == 0x10);
				setFlagC(result > 0xff);
				break;
			case ADD_16:
				setFlagN(false);
				setFlagH((source & 0x0fff) + (source2 & 0x0fff) > 0x0fff);
				setFlagC(source + source2 > 0xffff);
				setRegister(cpu, register, (source + source2) & 0xffff);
				break;
			case ADC:
				carry = (getFlagC() ? 1 : 0);
				hFlag = ((source & 0x0f) + (source2 & 0x0f) + carry > 0x0f);
				result = source + source2 + carry;
				cFlag = result > 0xff;
				setFlagZ((result & 0xFF) == 0);
				setFlagN(false);
				setFlagH(hFlag);
				setFlagC(cFlag);
				setAccumulator(cpu, result);
				break;
			case SUB:
				accum = getAccumulator();
				setFlagZ(((accum - source) & 0xff) == 0);
				setFlagN(true);
				setFlagH((0x0f & source) > (0x0f & accum));
				setFlagC(source > accum);
				setAccumulator(cpu, ((accum - source) & 0xff));
				break;
			case SBC:
				carry = (getFlagC() ? 1 : 0);
				result = getAccumulator() - source - carry;
				setFlagZ((result & 0xff) == 0);
				setFlagN(true);
				setFlagH(((getAccumulator()  ^ source ^ (result & 0xff)) & (1 << 4)) != 0);
				setFlagC(result < 0);
				setAccumulator(cpu, result & 0xff);
				break;
			case PUSH:
				cpu.pushSP(getRegister(cpu, register));
				break;
			case POP:
				setRegister(cpu, register, cpu.popSP());
				break;
			case INC:
				result = (source + 1) & 0xff;
				setRegister(cpu, register, result);
				setFlagZ(result == 0);
				setFlagN(false);
				setFlagH((source & 0x0f) == 0x0f);
				break;
			case DEC:
				result = (source - 1) & 0xff;
				setRegister(cpu, register, result);
				setFlagZ(result == 0);
				setFlagN(false);
				setFlagH((source & 0x0f) == 0x0);
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
			case DAA:
				accum = getAccumulator();
				if (getFlagN()) {
					if (getFlagH()) {
						accum = (accum - 6) & 0xff;
					}
					if (getFlagC()) {
						accum = (accum - 0x60) & 0xff;
					}
				} else {
					if (getFlagH() || (accum & 0xf) > 9) {
						accum += 0x06;
					}
					if (getFlagC() || accum > 0x9f) {
						accum += 0x60;
					}
				}
				setFlagH(false);
				if (accum > 0xff) {
					setFlagC(true);
				}
				accum &= 0xff;
				setFlagZ(accum == 0);
				setAccumulator(cpu, accum);
				break;
			default:
				throw new UnsupportedOperationException("Math function not implemented");
			}
		return cycles;
	}

 	
	

}

package cpu.opcodetypes;

import cpu.GameBoyCPU;
import cpu.opcodetypes.enums.OpCodeFunction;
import cpu.opcodetypes.enums.OpCodeRegister;
import mmu.GameBoyMMU;

public class OpCodeBit extends OpCode {
	
	private OpCodeFunction function;
	private int bitPosition;
	private OpCodeRegister register;

	public OpCodeBit(String doc, int cycles, int instructionSize, OpCodeFunction function, OpCodeRegister register, int position ) {
		super(doc, cycles, instructionSize);
		this.function = function;
		this.bitPosition = position;
		this.register = register;
		
	}

	public OpCodeBit(String doc, int cycles, int instructionSize, OpCodeFunction function, OpCodeRegister register) {
		super(doc, cycles, instructionSize);
		this.function = function;
		this.register = register;
	}

	@Override
	public int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception {
		int result;
		switch(function) {
			case BIT:
				setFlagZ(!getBitFromRegister(register, bitPosition));
				setFlagN(false);
				setFlagH(true);
				break;
			case RL:
				result = (getRegister(cpu, register) << 1) & 0xff;
				result |= getFlagC() ? 1 : 0;
				setFlagC((getRegister(cpu, register) & (1<<7)) != 0);
				setRegister(cpu, register, result & 0xff);
				setFlagZ(result == 0);
				setFlagN(false);
				setFlagH(false);
				break;
			case RLC:
				result = (getRegister(cpu, register) << 1) & 0xff;
				if ((getRegister(cpu, register) & (1<<7)) != 0) {
					result |= 1;
					setFlagC(true);
				} else {
					setFlagC(false);
				}

				setRegister(cpu, register, result & 0xff);
				setFlagZ(result == 0);
				setFlagN(false);
				setFlagH(false);
				break;
			case RRC:
				result = getRegister(cpu, register) >> 1;
				if ((getRegister(cpu, register) & 1) == 1) {
					result |= (1 << 7);
					setFlagC(true);
				} else {
					setFlagC(false);
				}
				setRegister(cpu, register, result & 0xff);
				setFlagZ(result == 0);
				setFlagN(false);
				setFlagH(false);
				break;
			case RR:
				result = getRegister(cpu, register) >> 1;
				result |= getFlagC() ? (1 << 7) : 0;

				setFlagC((getRegister(cpu, register) & 1) != 0);
				setRegister(cpu, register, result & 0xff);
				setFlagZ(result == 0);
				setFlagN(false);
				setFlagH(false);
				break;
			default:
				throw new UnsupportedOperationException("Not implemented");
		}
		return cycles;
	}

}

package cpu.opcodetypes;

import cpu.GameBoyCPU;
import cpu.opcodetypes.enums.OpCodeRegister;
import main.Util;
import mmu.GameBoyMMU;

import static cpu.opcodetypes.enums.OpCodeRegister.REGISTER_C;

public abstract class OpCode {


	protected int cycles;
	protected int instructionSize;
	protected int programAddress;
	
	public OpCode(int cycles, int instructionSize) {
		this.cycles = cycles;
		this.instructionSize = instructionSize;
	}
	
	/**
	 * 
	 * @param cpu
	 * @param mmu
	 * @return the amount of cycles produced
	 */
	public abstract int runCode(GameBoyCPU cpu, GameBoyMMU mmu) throws Exception;
	
	/**
	 * @param offset of which memory position to read,
	 * relative to the address of the current opcode
	 * @return the memory at that position
	 */
	public int getRelativeMemory(GameBoyCPU cpu, int offset) {
		return Util.getMemory().getMemoryAtAddress(cpu.getProgramCounter() + offset);
	}

	public int getInstructionSize() {
		return instructionSize;
	}
	
	public void setRegister(GameBoyCPU cpu, OpCodeRegister register, int source) throws Exception {
		int address = 0;
		byte data = (byte)source;
		switch (register) {
		case REGISTER_A:
			cpu.setA(data);
			break;
		case REGISTER_B:
			cpu.setB(data);
			break;
		case REGISTER_C:
			cpu.setC(data);
			break;
		case REGISTER_D:
			cpu.setD(data);
			break;
		case REGISTER_E:
			cpu.setE(data);
			break;
		case REGISTER_H:
			cpu.setH(data);
			break;
		case REGISTER_L:
			cpu.setL(data);
			break;
		case REGISTERS_HL:
			cpu.setHL(data);
			break;
		case REGISTERS_SP:
			cpu.setSP(data);
			break;
		case REGISTERS_BC:
			cpu.setBC(data);
			break;
		case REGISTERS_DE:
			cpu.setDE(data);
			break;
		case ADDRESS_HL:
			address = cpu.getHL();
			Util.getMemory().setMemoryAtAddress(address, source);
			break;
		case ADDRESS_HL_INC:
			address = cpu.getHL();
			cpu.setHL(address + 1);
			Util.getMemory().setMemoryAtAddress(address, source);
			break;
		case ADDRESS_HL_DEC:
			address = cpu.getHL();
			cpu.setHL(address - 1);
			Util.getMemory().setMemoryAtAddress(address, source);
			break;
		case ADDRESS_FF00_C:
			address = 0xFF00 + getRegister(cpu, REGISTER_C);
			Util.getMemory().setMemoryAtAddress(address, source);
			break;
		case LDH_ADDRESS_FF00: // strange edge cases
			address = 0xFF00 + source;
			Util.getMemory().setMemoryAtAddress(address, cpu.getA());
			break;
		case LDH_ADDRESS_FF00_REGISTER_A:
			address = 0xFF00 + source;
			cpu.setA(Util.getMemory().getMemoryAtAddress(address));
			break;
		case REGISTER_F:
			throw new Exception("Invalid Register Access");
		default:
			throw new UnsupportedOperationException("setRegister() missing register " + register.name());
		}
	}
	

	public int getRegister(GameBoyCPU cpu, OpCodeRegister register) throws Exception {
		int address;
		switch (register) {
		case REGISTER_A:
			return cpu.getA();
		case REGISTER_B:
			return cpu.getB();
		case REGISTER_C:
			return cpu.getC();
		case REGISTER_D:
			return cpu.getD();
		case REGISTER_E:
			return cpu.getE();
		case REGISTER_H:
			return cpu.getH();
		case REGISTER_L:
			return cpu.getL();
		case REGISTERS_HL:
			return cpu.getHL();
		case REGISTERS_SP:
			return cpu.getSP();
		case REGISTERS_BC:
			return cpu.getBC();
		case REGISTERS_DE:
			return cpu.getDE();
		case REGISTER_F:
			return cpu.getF();
		case ADDRESS_HL:
			address = cpu.getHL();
			return Util.getMemory().getMemoryAtAddress(address);
		case ADDRESS_BC:
			address = cpu.getBC();
			return Util.getMemory().getMemoryAtAddress(address);
		case ADDRESS_DE:
			address = cpu.getDE();
			return Util.getMemory().getMemoryAtAddress(address);
		case ADDRESS_HL_INC:
			address = cpu.getHL();
			cpu.setHL(address + 1);
			return Util.getMemory().getMemoryAtAddress(address);
		case ADDRESS_HL_DEC:
			address = cpu.getHL();
			cpu.setHL(address - 1);
			return Util.getMemory().getMemoryAtAddress(address);
		case ADDRESS_FF00_C:
			address = 0xFF00 + getRegister(cpu, REGISTER_C);
			return Util.getMemory().getMemoryAtAddress(address);
		default:
			throw new UnsupportedOperationException("getRegister() missing register " + register.name());
		}
	}
	
	public int getAccumulator() {
		return Util.getCPU().getA();
	}
	
	public void setFlagC(boolean set) {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		if(set) {
			cpu.setF(Util.setBit(f, 4));
		}
		else {
			cpu.setF(Util.unsetBit(f, 4));
		}
	}
	public void setFlagH(boolean set) {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		if(set) {
			cpu.setF(Util.setBit(f, 5));
		}
		else {
			cpu.setF(Util.unsetBit(f, 5));
		}
	}
	public void setFlagN(boolean set) {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		if(set) {
			cpu.setF(Util.setBit(f, 6));
		}
		else {
			cpu.setF(Util.unsetBit(f, 6));
		}
	}
	public void setFlagZ(boolean set) {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		if(set) {
			cpu.setF(Util.setBit(f, 7));
		}
		else {
			cpu.setF(Util.unsetBit(f, 7));
		}
	}
	
	public boolean getFlagC() {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		return Util.getBit(f, 4);
	}
	public boolean getFlagH() {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		return Util.getBit(f, 5);
	}

	public boolean getFlagN() {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		return Util.getBit(f, 6);
	}

	public boolean getFlagZ() {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		return Util.getBit(f, 7);
	}
	
	public boolean getBitFromRegister(OpCodeRegister register, int bitPos) throws Exception {
		return Util.getBit(getRegister(Util.getCPU(),register), bitPos);
	}
	
	public void setBitFromRegister(OpCodeRegister register, int bitPos, boolean value) throws Exception {
		setRegister(Util.getCPU(), register, Util.setBit(getRegister(Util.getCPU(),register), bitPos));
	}
	

	public void setAccumulator(GameBoyCPU cpu, int i) {
		cpu.setA(i);		
	}

	/**
	 *
	 * @param c set Flag C (carry)
	 * @param h set Flag H (half carry)
	 * @param n set Flag N (subtraction flag? not sure but maybe the opposite of carry)
	 * @param z set Flag Z (zero flag)
	 */
	public void setFlags(boolean c, boolean h, boolean n, boolean z) {
		setFlagC(c);
		setFlagH(h);
		setFlagN(n);
		setFlagZ(z);
	}

	/**
	 *
	 * @param cpu
	 * @param register careful because this is unchecked, can be used with any register depspite only intended for
	 * @param data
	 * @throws Exception
	 */
	public void push(GameBoyCPU cpu, OpCodeRegister register, int data) throws Exception {
		int leftByte = (byte)(data & 0xff);
		int rightByte = (byte)((data >> 8) & 0xFF);
		setRegister(cpu, register, getRegister(cpu, register) - 1);
		Util.getMemory().setMemoryAtAddress(getRegister(cpu, register), leftByte);
		setRegister(cpu, register, getRegister(cpu, register) - 1);
		Util.getMemory().setMemoryAtAddress(getRegister(cpu, register), rightByte);
	}

	public int pop(GameBoyCPU cpu, OpCodeRegister register) throws Exception {
		int leftByte = Util.getMemory().getMemoryAtAddress(getRegister(cpu, register));
		setRegister(cpu, register, getRegister(cpu, register) + 1);
		int rightByte = Util.getMemory().getMemoryAtAddress(getRegister(cpu, register));
		setRegister(cpu, register, getRegister(cpu, register) + 1);
		return (leftByte << 8 | rightByte);
	}
}

package cpu.opcodetypes;

import cpu.GameBoyCPU;
import main.Utility;
import mmu.GameBoyMMU;

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
	public int getRelativeMemory(int offset) {
		return Utility.getMemory().getMemoryAtAddress(programAddress + offset);
	}

	public int getInstructionSize() {
		return instructionSize;
	}
	
	public void setRegister(GameBoyCPU cpu, OpCodeRegister register, int source) throws Exception {
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
		case REGISTER_F:
			throw new Exception("Invalid Register Access");
		default:
			break;
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
			return Utility.getMemory().getMemoryAtAddress(address);
		case ADDRESS_BC:
			address = cpu.getBC();
			return Utility.getMemory().getMemoryAtAddress(address);
		case ADDRESS_DE:
			address = cpu.getDE();
			return Utility.getMemory().getMemoryAtAddress(address);
		case ADDRESS_HL_INC:
			address = cpu.getHL();
			cpu.setHL(address + 1);
			return Utility.getMemory().getMemoryAtAddress(address);
		case ADDRESS_HL_DEC:
			address = cpu.getHL();
			cpu.setHL(address - 1);
			return Utility.getMemory().getMemoryAtAddress(address);
		default:
			throw new Exception("invalid data location");
		}
	}
	
	public int getAccumulator() {
		return Utility.getCPU().getA();
	}
	
	public void setFlagC(boolean set) {
		GameBoyCPU cpu = Utility.getCPU();
		int f = cpu.getF();
		cpu.setF(Utility.setBit(f, 4));
	}
	public void setFlagH(boolean set) {
		GameBoyCPU cpu = Utility.getCPU();
		int f = cpu.getF();
		cpu.setF(Utility.setBit(f, 5));
	}
	public void setFlagN(boolean set) {
		GameBoyCPU cpu = Utility.getCPU();
		int f = cpu.getF();
		cpu.setF(Utility.setBit(f, 6));
	}
	public void setFlagZ(boolean set) {
		GameBoyCPU cpu = Utility.getCPU();
		int f = cpu.getF();
		cpu.setF(Utility.setBit(f, 7));
	}
	
	public boolean getFlagC() {
		GameBoyCPU cpu = Utility.getCPU();
		int f = cpu.getF();
		return Utility.getBit(f, 4);
	}
	public boolean getFlagH() {
		GameBoyCPU cpu = Utility.getCPU();
		int f = cpu.getF();
		return Utility.getBit(f, 5);
	}

	public boolean getFlagN() {
		GameBoyCPU cpu = Utility.getCPU();
		int f = cpu.getF();
		return Utility.getBit(f, 6);
	}

	public boolean getFlagZ() {
		GameBoyCPU cpu = Utility.getCPU();
		int f = cpu.getF();
		return Utility.getBit(f, 7);
	}
	
	public boolean getBitFromRegister(OpCodeRegister register, int bitPos) throws Exception {
		return Utility.getBit(getRegister(Utility.getCPU(),register), bitPos);
	}
	
	public void setBitFromRegister(OpCodeRegister register, int bitPos, boolean value) throws Exception {
		setRegister(Utility.getCPU(), register, Utility.setBit(getRegister(Utility.getCPU(),register), bitPos));
	}
	

	public void setAccumulator(GameBoyCPU cpu, int i) {
		cpu.setA(i);		
	}
	
	
	public void setFlags(boolean c, boolean h, boolean n, boolean z) {
		setFlagC(c);
		setFlagH(h);
		setFlagC(n);
		setFlagH(z);
	}
}

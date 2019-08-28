package cpu;

import main.Utility;
import mmu.GameBoyMMU;

public class OpCode {



	public OpCodeType type;
	public OpCodeType sourceType;
	public OpCodeType sourceType2;
	public OpCodeType destType;
	public OpCodeType dest;
	public OpCodeType dest2;
	public byte source;
	public byte source2;
	public int destAddress = -1;
	private GameBoyCPU cpu;

	public OpCode(GameBoyCPU cpu, byte code, int opAddress) {
		this.cpu = cpu;
		String hex = Utility.byteToHex(code);
		System.out.println("creating OpCode " + hex);
		int instructionSize = 0;
		switch(hex) {
		case "00":
			instructionSize = 1;
			type = OpCodeType.OP_NOP;
			break;
		case "01":
			instructionSize = 3;
			type = OpCodeType.OP_LD;
			destType = OpCodeType.DEST_TYPE_PAIR;
			dest = OpCodeType.DEST_R_B;
			dest2 = OpCodeType.DEST_R_C;
			source = cpu.mmu.getMemoryAtAddress(opAddress + 1);
			source2 = cpu.mmu.getMemoryAtAddress(opAddress + 2);
		case "31":
			instructionSize = 3;
			type = OpCodeType.OP_LD;
			destType = OpCodeType.DEST_TYPE_PAIR;
			dest = OpCodeType.DEST_R_S;
			dest2 = OpCodeType.DEST_R_P;
			source = cpu.mmu.getMemoryAtAddress(opAddress + 1);
			source2 = cpu.mmu.getMemoryAtAddress(opAddress + 2);
			
			break;
		case "10":
			type = OpCodeType.OP_STOP;
			break;
		case "AF":
			instructionSize = 1;
			type = OpCodeType.OP_XOR;
			dest = OpCodeType.DEST_R_A;
			break;
		}
		cpu.programCounter += instructionSize;
	}

	public byte getSource() {
		// TODO Auto-generated method stub
		return 0;
	}

	public OpCodeType getDestType() {
		return destType;
	}

	public int runCode(GameBoyMMU mmu) {
		System.out.println("test1");
		if(type == OpCodeType.OP_NOP) {
			return 4;
		}
		else if(type == OpCodeType.OP_LD) {
			return opLoad();
		}
		else if(type == OpCodeType.OP_XOR) {
			return opXor(dest);
		}
		return 4;

	} 
	
	private int opXor(OpCodeType register) {
		System.out.println(register.name());
		int defaultByte = cpu.getA();
		//I'm dumb, and I'm gonna assume that "a" is the value you xor against
		switch(register) {
		case DEST_R_A:
			// xorring a^a will return 0x00
			System.out.println("test");
			cpu.setA((byte) 0x00);
			break;
		case DEST_R_B:
			cpu.setB((byte)(cpu.getB()^defaultByte));
			break;
		case DEST_R_C:
			cpu.setC((byte)(cpu.getC()^defaultByte));
			break;
		case DEST_R_D:
			cpu.setD((byte)(cpu.getD()^defaultByte));
			break;
		case DEST_R_E:
			cpu.setE((byte)(cpu.getD()^defaultByte));
			break;
		case DEST_R_F:
			cpu.setF((byte)(cpu.getD()^defaultByte));
			break;
		case DEST_ADDR_HL:
			int address = cpu.getHLAddress();
			cpu.mmu.setMemoryAtAddress(address, (byte) (cpu.mmu.getMemoryAtAddress(address)^defaultByte));
			return 8;
		default:
			break;
		}
		return 4;
	}

	public int opLoad() {
		switch(getDestType()) {
		case DEST_TYPE_ADDRESS:
//			int address = 0;
//			address = Utility.bytesToAddress(dest2, dest);
//			byte data = source;
//			cpu.mmu.setMemoryAtAddress(address, data);
			//TODO
			return 12;
		case DEST_TYPE_PAIR:
			editRegister(dest, source2);
			editRegister(dest2, source);
			return 12;
		case DEST_TYPE_REGISTER:
			editRegister(dest, source);
			return 4;
		default:
			return 4;

		}
	}

	public void editRegister(OpCodeType register, byte data ) {
		switch(register) {
		case DEST_R_A:
			cpu.setA(data);
			break;
		case DEST_R_B:
			cpu.setB(data);
			break;
		case DEST_R_C:
			cpu.setC(data);
			break;
		case DEST_R_D:
			cpu.setD(data);
			break;
		case DEST_R_E:
			cpu.setE(data);
			break;
		case DEST_R_F:
			cpu.setF(data);
			break;
		case DEST_R_H:
			cpu.setH(data);
			break;
		case DEST_R_L:
			cpu.setL(data);
			break;
		case DEST_R_S:
			cpu.setS(data);
			break;
		case DEST_R_P:
			cpu.setP(data);
			break;
		default:
			break;
		}

	}
}
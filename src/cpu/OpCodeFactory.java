package cpu;

import java.util.HashMap;
import java.util.Map;

import cpu.opcodetypes.*;
import cpu.opcodetypes.enums.JumpType;
import cpu.opcodetypes.enums.OpCodeCondition;
import cpu.opcodetypes.enums.OpCodeFunction;
import cpu.opcodetypes.enums.OpCodeRegister;
import main.Util;


public class OpCodeFactory {

	private static OpCodeFactory singletonFactory;
	private Map<String, OpCode> primaryOpCodeMap = new HashMap<>();
	private Map<String, OpCode> CBOpCodeMap = new HashMap<>();
	
	private OpCodeFactory() {

		//NOP: do nothing
		primaryOpCodeMap.put("00", new OpCodeNOP());

		//TODO understand stop, probably when I figure out interrupts
		primaryOpCodeMap.put("10", new OpCodeSTOP());

		//JUMPS
		primaryOpCodeMap.put("18", new OpCodeJump("JR", 2, JumpType.ADD_TO_ADDRESS));
		primaryOpCodeMap.put("20", new OpCodeJump("JR NZ", 2, JumpType.ADD_TO_ADDRESS, OpCodeCondition.NZ));
		primaryOpCodeMap.put("28", new OpCodeJump("JR Z", 2, JumpType.ADD_TO_ADDRESS, OpCodeCondition.Z));

		//XOR
		primaryOpCodeMap.put("af", new OpCodeMath("XOR A",4, 1, OpCodeFunction.XOR, OpCodeRegister.REGISTER_A));

		//INC
		primaryOpCodeMap.put("13", new OpCodeMath("INC DE", 4, 1, OpCodeFunction.INC_16, OpCodeRegister.REGISTERS_DE));
		primaryOpCodeMap.put("23", new OpCodeMath("INC HL", 4, 1, OpCodeFunction.INC_16, OpCodeRegister.REGISTERS_HL));
		primaryOpCodeMap.put("24", new OpCodeMath("INC H", 4, 1, OpCodeFunction.INC, OpCodeRegister.REGISTER_H));
		primaryOpCodeMap.put("0c", new OpCodeMath("INC C", 4, 1, OpCodeFunction.INC, OpCodeRegister.REGISTER_C));

		//DEC
		primaryOpCodeMap.put("05", new OpCodeMath("DEC B", 4, 1, OpCodeFunction.DEC, OpCodeRegister.REGISTER_B));
		primaryOpCodeMap.put("0d", new OpCodeMath("DEC C", 4, 1, OpCodeFunction.DEC, OpCodeRegister.REGISTER_C));
		primaryOpCodeMap.put("1d", new OpCodeMath("DEC E", 4, 1, OpCodeFunction.DEC, OpCodeRegister.REGISTER_E));
		primaryOpCodeMap.put("15", new OpCodeMath("DEC D", 4, 1, OpCodeFunction.DEC, OpCodeRegister.REGISTER_D));
		primaryOpCodeMap.put("3d", new OpCodeMath("DEC A", 4, 1, OpCodeFunction.DEC, OpCodeRegister.REGISTER_A));

		//CP
		primaryOpCodeMap.put("fe", new OpCodeMath("CP d8", 8, 2, OpCodeFunction.CP));
		primaryOpCodeMap.put("be", new OpCodeMath("CP (HL))", 8, 1, OpCodeFunction.CP, OpCodeRegister.ADDRESS_HL));

		//SUB
		primaryOpCodeMap.put("90", new OpCodeMath("SUB B", 4, 1, OpCodeFunction.SUB, OpCodeRegister.REGISTER_B));

		//RLA
		primaryOpCodeMap.put("17", new OpCodeBit("RLA",4, 1, OpCodeFunction.RL, OpCodeRegister.REGISTER_A));
		//RLCA
		primaryOpCodeMap.put("04", new OpCodeBit("RLCA",4, 1, OpCodeFunction.RLC, OpCodeRegister.REGISTER_A));

		//C1-F1 pops, C5-F5 pushes to/from register to/from SP
		primaryOpCodeMap.put("c5", new OpCodeMath("PUSH BC", 16, 1, OpCodeFunction.PUSH, OpCodeRegister.REGISTERS_BC));
		primaryOpCodeMap.put("d5", new OpCodeMath("PUSH DE",16, 1, OpCodeFunction.PUSH, OpCodeRegister.REGISTERS_DE));
		primaryOpCodeMap.put("e5", new OpCodeMath("PUSH HL",16, 1, OpCodeFunction.PUSH, OpCodeRegister.REGISTERS_HL));
		primaryOpCodeMap.put("f5", new OpCodeMath("PUSH AF",16, 1, OpCodeFunction.PUSH, OpCodeRegister.REGISTERS_AF));

		primaryOpCodeMap.put("c1", new OpCodeMath("POP BC",12, 1, OpCodeFunction.POP, OpCodeRegister.REGISTERS_BC));
		primaryOpCodeMap.put("d2", new OpCodeMath("POP DE",12, 1, OpCodeFunction.POP, OpCodeRegister.REGISTERS_DE));
		primaryOpCodeMap.put("e3", new OpCodeMath("POP HL",12, 1, OpCodeFunction.POP, OpCodeRegister.REGISTERS_HL));
		primaryOpCodeMap.put("f4", new OpCodeMath("POP AF",12, 1, OpCodeFunction.POP, OpCodeRegister.REGISTERS_AF));

		//CALLS
		primaryOpCodeMap.put("cd", new OpCodeJump("CALL", 3, JumpType.CALL));

		//RETURNS
		primaryOpCodeMap.put("c9", new OpCodeJump("RET", 1, JumpType.RETURN));


		generateCBPrefixCodes();
		generateLDCodes();
	}

	private void generateLDCodes() {


		//x6 and xE loading registers from data (8 codes)
		primaryOpCodeMap.put("0e", new OpCodeLD("LD C,d8",8, 2, OpCodeRegister.REGISTER_C));
		primaryOpCodeMap.put("06", new OpCodeLD("LD B,d8",8, 2, OpCodeRegister.REGISTER_B));
		primaryOpCodeMap.put("3e", new OpCodeLD("LD A,d8",8, 2, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("2e", new OpCodeLD("LD L,d8",8, 2, OpCodeRegister.REGISTER_L));
		primaryOpCodeMap.put("1e", new OpCodeLD("LD L,d8",8, 2, OpCodeRegister.REGISTER_E));

		//xA loading A from address (4 codes)
		primaryOpCodeMap.put("1a", new OpCodeLD("LD A,(DE)",12, 1, OpCodeRegister.REGISTER_A, OpCodeRegister.ADDRESS_DE));

		//x2 loading A into address
		primaryOpCodeMap.put("22", new OpCodeLD("LD (HL+),A", 8, 1, OpCodeRegister.ADDRESS_HL_INC, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("32", new OpCodeLD("LD (HL-),A", 8, 1, OpCodeRegister.ADDRESS_HL_DEC, OpCodeRegister.REGISTER_A));

		//x1 loading 16bit registers
		primaryOpCodeMap.put("01", new OpCodeLD("LD BC,d16",12, 3, OpCodeRegister.REGISTERS_BC));
		primaryOpCodeMap.put("11", new OpCodeLD("LD DE,d16",12, 3, OpCodeRegister.REGISTERS_DE));
		primaryOpCodeMap.put("21", new OpCodeLD("LD HL,d16",12, 3, OpCodeRegister.REGISTERS_HL));
		primaryOpCodeMap.put("31", new OpCodeLD("LD SP,d16",12, 3, OpCodeRegister.REGISTERS_SP));

		//4x-7x (minus 0x76 which is a halt) loading register into register and sometimes HL arithmetic
		primaryOpCodeMap.put("4f", new OpCodeLD("LD C,A",4, 1, OpCodeRegister.REGISTER_C, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("57", new OpCodeLD("LD D,A",4, 1, OpCodeRegister.REGISTER_D, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("67", new OpCodeLD("LD H,A",4, 1, OpCodeRegister.REGISTER_H, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("7b", new OpCodeLD("LD E,A",4, 1, OpCodeRegister.REGISTER_A, OpCodeRegister.REGISTER_E));
		primaryOpCodeMap.put("7c", new OpCodeLD("LD A,H",4, 1, OpCodeRegister.REGISTER_A, OpCodeRegister.REGISTER_H));
		primaryOpCodeMap.put("77", new OpCodeLD("LD (HL),A",8, 1, OpCodeRegister.ADDRESS_HL, OpCodeRegister.REGISTER_A));


		primaryOpCodeMap.put("e0", new OpCodeLD("LDH ($FF00+a8), A",12, 2, OpCodeRegister.LDH_ADDRESS_FF00));
		primaryOpCodeMap.put("e2", new OpCodeLD("LD ($FF00+C), A",8, 1, OpCodeRegister.ADDRESS_FF00_C, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("ea", new OpCodeLD("LD (a16), A",16, 3, OpCodeRegister.ADDRESS_A_TO_DATA));
		primaryOpCodeMap.put("fa", new OpCodeLD("LD A,(a16)",16, 3, OpCodeRegister.ADDRESS_DATA_TO_A));
		primaryOpCodeMap.put("f0", new OpCodeLD("LDH a, ($FF00+a8)",12, 2, OpCodeRegister.LDH_ADDRESS_FF00_REGISTER_A));

	}

	private void generateCBPrefixCodes() {
		CBOpCodeMap.put("7c", new OpCodeBit("BIT H,7",8, 2, OpCodeFunction.BIT, OpCodeRegister.REGISTER_H, 7));
		CBOpCodeMap.put("11", new OpCodeBit("RL C",8, 2, OpCodeFunction.RL, OpCodeRegister.REGISTER_C));
		
	}

	public static OpCodeFactory getInstance() {
		if (singletonFactory == null) {
			singletonFactory = new OpCodeFactory();
		}
		return singletonFactory;
	}
	
	
	
	public OpCode constructOpCode(int programCounter, int i) {

		String hexString = Util.byteToHex(i);
		if (hexString.equals("cb")) {
			int nextCode = Util.getMemory().getMemoryAtAddress(programCounter + 1);
			String hexCBOp = Util.byteToHex(nextCode);
			return CBOpCodeMap.get(hexCBOp);
		}
		return primaryOpCodeMap.get(hexString);
	}
}
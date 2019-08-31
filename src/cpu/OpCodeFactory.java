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
		primaryOpCodeMap.put("20", new OpCodeJump(2, JumpType.ADD_TO_ADDRESS, OpCodeCondition.NZ));
		primaryOpCodeMap.put("af", new OpCodeMath(4, 1, OpCodeFunction.XOR, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("cd", new OpCodeJump(3, JumpType.CALL));

		generateCBPrefixCodes();
		generateLDCodes();
	}

	private void generateLDCodes() {


		//x6 and xE loading registers from data (8 codes)
		primaryOpCodeMap.put("0e", new OpCodeLD(8, 2, OpCodeRegister.REGISTER_C));
		primaryOpCodeMap.put("06", new OpCodeLD(8, 2, OpCodeRegister.REGISTER_B));
		primaryOpCodeMap.put("3e", new OpCodeLD(8, 2, OpCodeRegister.REGISTER_A));

		//xA loading A from address (4 codes)
		primaryOpCodeMap.put("1a", new OpCodeLD(12, 1, OpCodeRegister.REGISTER_A, OpCodeRegister.ADDRESS_DE));

		//x2 loading A into address
		primaryOpCodeMap.put("32", new OpCodeLD(12, 1, OpCodeRegister.ADDRESS_HL_DEC, OpCodeRegister.REGISTER_A));

		//x1 loading 16bit registers
		primaryOpCodeMap.put("01", new OpCodeLD(12, 3, OpCodeRegister.REGISTERS_BC));
		primaryOpCodeMap.put("11", new OpCodeLD(12, 3, OpCodeRegister.REGISTERS_DE));
		primaryOpCodeMap.put("21", new OpCodeLD(12, 3, OpCodeRegister.REGISTERS_HL));
		primaryOpCodeMap.put("31", new OpCodeLD(12, 3, OpCodeRegister.REGISTERS_SP));

		//4x-7x (minus 0x76 which is a halt) loading register into register and sometimes HL arithmetic
		primaryOpCodeMap.put("4f", new OpCodeLD(4, 1, OpCodeRegister.REGISTER_C, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("77", new OpCodeLD(8, 1, OpCodeRegister.ADDRESS_HL, OpCodeRegister.REGISTER_A));


		primaryOpCodeMap.put("e0", new OpCodeLD(12, 2, OpCodeRegister.LDH_ADDRESS_FF00));
		primaryOpCodeMap.put("e2", new OpCodeLD(8, 2, OpCodeRegister.ADDRESS_FF00_C, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("f0", new OpCodeLD(12, 2, OpCodeRegister.LDH_ADDRESS_FF00_REGISTER_A));

	}

	private void generateCBPrefixCodes() {
		CBOpCodeMap.put("7c", new OpCodeBit(8, 2, OpCodeFunction.BIT, OpCodeRegister.REGISTER_H, 7));
		
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
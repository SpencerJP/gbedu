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
		primaryOpCodeMap.put("00", new OpCodeNOP());
		primaryOpCodeMap.put("10", new OpCodeSTOP());
		primaryOpCodeMap.put("01", new OpCodeLD(12, 3, OpCodeRegister.REGISTERS_BC));
		primaryOpCodeMap.put("11", new OpCodeLD(12, 3, OpCodeRegister.REGISTERS_DE));
		primaryOpCodeMap.put("20", new OpCodeJump(2, JumpType.ADD_TO_ADDRESS, OpCodeCondition.NZ));
		primaryOpCodeMap.put("21", new OpCodeLD(12, 3, OpCodeRegister.REGISTERS_HL));
		primaryOpCodeMap.put("31", new OpCodeLD(12, 3, OpCodeRegister.REGISTERS_SP));
		primaryOpCodeMap.put("32", new OpCodeLD(12, 1, OpCodeRegister.ADDRESS_HL_DEC, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("af", new OpCodeMath(4, 1, OpCodeFunction.XOR, OpCodeRegister.REGISTER_A));
		
		generateCBCodes();
	}

	private void generateCBCodes() {
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
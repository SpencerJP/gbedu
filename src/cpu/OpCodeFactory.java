package cpu;

import java.util.HashMap;
import java.util.Map;

import opcodetypes.OpCodeNOP;
import cpu.opcodetypes.*;
import main.Utility;
import mmu.GameBoyMMU;

public class OpCodeFactory {

	private static OpCodeFactory singletonFactory;
	
	private OpCodeFactory() {
	}

	public static OpCodeFactory getInstance() {
		if (singletonFactory == null) {
			singletonFactory = new OpCodeFactory();
		}
		return singletonFactory;
	}
	
	
	
	public OpCode constructOpCode(int i, int codeAddress) {

		String hexString = Utility.byteToHex(i);
		switch(hexString) {
		case "00":
			return new OpCodeNOP(4, 1, codeAddress);
		case "01":		
			return new OpCodeLD(12, 3, codeAddress, OpCodeRegister.REGISTERS_BC);
		case "11":
			return new OpCodeLD(12, 3, codeAddress, OpCodeRegister.REGISTERS_DE);
		case "21":
			return new OpCodeLD(12, 3, codeAddress, OpCodeRegister.REGISTERS_HL);
		case "31":
			return new OpCodeLD(12, 3, codeAddress, OpCodeRegister.REGISTERS_SP);	
		case "10":
			return new OpCodeSTOP(codeAddress);
		case "af":
			return new OpCodeMath(4, 1, codeAddress, OpCodeFunction.XOR, OpCodeRegister.REGISTER_A);
		}
		return null;
	}
}
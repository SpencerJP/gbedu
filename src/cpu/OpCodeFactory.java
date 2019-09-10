package cpu;

import cpu.opcodetypes.*;
import cpu.opcodetypes.enums.*;
import main.Util;

import java.util.HashMap;
import java.util.Map;


public class OpCodeFactory {

	private static OpCodeFactory singletonFactory;
	private Map<String, OpCode> primaryOpCodeMap = new HashMap<>();
	private Map<String, OpCode> CBOpCodeMap = new HashMap<>();

    private static final Map<String, OpCodeRegister> RegisterMap = new HashMap<String, OpCodeRegister>() {
        {
            put("a", OpCodeRegister.REGISTER_A);
            put("b", OpCodeRegister.REGISTER_B);
            put("c", OpCodeRegister.REGISTER_C);
            put("d", OpCodeRegister.REGISTER_D);
            put("e", OpCodeRegister.REGISTER_E);
            put("f", OpCodeRegister.REGISTER_F);
            put("h", OpCodeRegister.REGISTER_H);
            put("l", OpCodeRegister.REGISTER_L);
            put("hl", OpCodeRegister.REGISTERS_HL);
            put("sp", OpCodeRegister.REGISTERS_SP);
            put("bc", OpCodeRegister.REGISTERS_BC);
            put("de", OpCodeRegister.REGISTERS_DE);
            put("af", OpCodeRegister.REGISTERS_AF);
            put("(hl)", OpCodeRegister.ADDRESS_HL);
            put("(bc)", OpCodeRegister.ADDRESS_BC);
            put("(de)", OpCodeRegister.ADDRESS_DE);
            put("(hl+)", OpCodeRegister.ADDRESS_HL_INC);
            put("(hl-)", OpCodeRegister.ADDRESS_HL_DEC);
        }
    };

    private static final Map<String, OpCodeFunction> FunctionMap = new HashMap<String, OpCodeFunction>() {
        {
            put("add", OpCodeFunction.ADD);
            put("adc", OpCodeFunction.ADC);
            put("sub", OpCodeFunction.SUB);
            put("sbc", OpCodeFunction.SBC);
            put("and", OpCodeFunction.AND);
            put("xor", OpCodeFunction.XOR);
            put("or", OpCodeFunction.OR);
            put("cp", OpCodeFunction.CP);
            put("inc", OpCodeFunction.INC);
            put("dec", OpCodeFunction.DEC);
            put("inc_16", OpCodeFunction.INC_16);
            put("dec_16", OpCodeFunction.DEC_16);
            put("push", OpCodeFunction.PUSH);
            put("pop", OpCodeFunction.POP);
        }
    };

    private static final Map<String, OpCodeBitFunction> BitFunctionMap = new HashMap<String, OpCodeBitFunction>() {
        {
            put("bit", OpCodeBitFunction.BIT);
            put("set", OpCodeBitFunction.SET);
            put("res", OpCodeBitFunction.RES);
            put("swap", OpCodeBitFunction.SWAP);
            put("rl", OpCodeBitFunction.RL);
            put("rlc", OpCodeBitFunction.RLC);
            put("sla", OpCodeBitFunction.SLA);
            put("srl", OpCodeBitFunction.SRL);
            put("rr", OpCodeBitFunction.RR);
            put("rrc", OpCodeBitFunction.RRC);
            put("sra", OpCodeBitFunction.SRA);
            put("cpl", OpCodeBitFunction.CPL);
            put("ccf", OpCodeBitFunction.CCF);
            put("scf", OpCodeBitFunction.SCF);
        }
    };
	
	private OpCodeFactory(){

		//NOP: do nothing
		primaryOpCodeMap.put("00", new OpCodeNOP());

		//TODO understand stop, probably when I figure out interrupts
		primaryOpCodeMap.put("10", new OpCodeSTOP());

		//JUMP RELATIVE TO ADDRESS
		primaryOpCodeMap.put("18", new OpCodeJump("JR", 2, JumpType.ADD_TO_ADDRESS));
		primaryOpCodeMap.put("20", new OpCodeJump("JR NZ", 2, JumpType.ADD_TO_ADDRESS, OpCodeCondition.NZ));
		primaryOpCodeMap.put("30", new OpCodeJump("JR NC", 2, JumpType.ADD_TO_ADDRESS, OpCodeCondition.NC));
		primaryOpCodeMap.put("28", new OpCodeJump("JR Z", 2, JumpType.ADD_TO_ADDRESS, OpCodeCondition.Z));
		primaryOpCodeMap.put("38", new OpCodeJump("JR C", 2, JumpType.ADD_TO_ADDRESS, OpCodeCondition.C));
		
		
		//JUMP TO ADDRESS
		primaryOpCodeMap.put("c3", new OpCodeJump("JP a16", 3, JumpType.JUMP_TO_ADDRESS));
		primaryOpCodeMap.put("e9", new OpCodeJump("JP (HL)", 1, JumpType.JUMP_TO_ADDRESS, OpCodeRegister.REGISTERS_HL));
        primaryOpCodeMap.put("ca", new OpCodeJump("JP Z a16", 3, JumpType.JUMP_TO_ADDRESS, OpCodeCondition.Z));
        primaryOpCodeMap.put("da", new OpCodeJump("JP C a16", 3, JumpType.JUMP_TO_ADDRESS, OpCodeCondition.C));
        primaryOpCodeMap.put("c2", new OpCodeJump("JP NZ a16", 3, JumpType.JUMP_TO_ADDRESS, OpCodeCondition.NZ));
        primaryOpCodeMap.put("d2", new OpCodeJump("JP NC a16", 3, JumpType.JUMP_TO_ADDRESS, OpCodeCondition.NC));

        //CALLS
        primaryOpCodeMap.put("cd", new OpCodeJump("CALL", 3, JumpType.CALL));

        //RETURNS
        primaryOpCodeMap.put("c9", new OpCodeJump("RET", 1, JumpType.RETURN));
        primaryOpCodeMap.put("c8", new OpCodeJump("RET Z", 1, JumpType.RETURN, OpCodeCondition.Z));
        primaryOpCodeMap.put("d8", new OpCodeJump("RET C", 1, JumpType.RETURN, OpCodeCondition.C));
        primaryOpCodeMap.put("c0", new OpCodeJump("RET NZ", 1, JumpType.RETURN, OpCodeCondition.NZ));
        primaryOpCodeMap.put("d0", new OpCodeJump("RET NC", 1, JumpType.RETURN, OpCodeCondition.NC));
        primaryOpCodeMap.put("d9", new OpCodeJump("RETI", 1, JumpType.RETI));

        //RESTART
        primaryOpCodeMap.put("ef", new OpCodeJump("RST 0x28", 1, JumpType.RESTART, 0x28));


        //INTERRUPT STUFF
        primaryOpCodeMap.put("f3", new OpCodeInterrupt("DI (disable interrupts)", 4, 1, InterruptCommands.DISABLE_INTERRUPTS));
        primaryOpCodeMap.put("fb", new OpCodeInterrupt("EI (enable interrupts)", 4, 1, InterruptCommands.ENABLE_INTERRUPTS));

        // math A, x functions
        String[] functionOrder = {"ADD", "ADC", "SUB", "SBC", "AND", "XOR", "OR", "CP"};
        String[] registerToRegisterOrder = {"B", "C", "D", "E", "H", "L", "(HL)", "A"};
        int startingHex = 0x80;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                String arg1 = functionOrder[i];
                String arg2 = registerToRegisterOrder[j];
                boolean setResultToAccumulator = (i == 0 || i == 1 || i == 3);
                String doc = setResultToAccumulator ? arg1 + " A," + arg2 : arg1 + " " + arg2;
                int cycles = 4;
                if (arg1.equals("(HL)") || arg2.equals("(HL)")) {
                    cycles = 8;
                }
                createOpCodeMath(Util.byteToHex(startingHex), doc, cycles, arg1, arg2, setResultToAccumulator);
                startingHex++;
            }
        }


		primaryOpCodeMap.put("e6", new OpCodeMath("AND d8",8, 2, OpCodeFunction.AND));

		//INC
        primaryOpCodeMap.put("03", new OpCodeMath("INC BC", 8, 1, OpCodeFunction.INC_16, OpCodeRegister.REGISTERS_BC));
		primaryOpCodeMap.put("13", new OpCodeMath("INC DE", 8, 1, OpCodeFunction.INC_16, OpCodeRegister.REGISTERS_DE));
        primaryOpCodeMap.put("23", new OpCodeMath("INC HL", 8, 1, OpCodeFunction.INC_16, OpCodeRegister.REGISTERS_HL));
        primaryOpCodeMap.put("33", new OpCodeMath("INC SP", 8, 1, OpCodeFunction.INC_16, OpCodeRegister.REGISTERS_SP));
		primaryOpCodeMap.put("24", new OpCodeMath("INC H", 4, 1, OpCodeFunction.INC, OpCodeRegister.REGISTER_H));
		primaryOpCodeMap.put("0c", new OpCodeMath("INC C", 4, 1, OpCodeFunction.INC, OpCodeRegister.REGISTER_C));
		primaryOpCodeMap.put("04", new OpCodeMath("INC B", 4, 1, OpCodeFunction.INC, OpCodeRegister.REGISTER_B));
        primaryOpCodeMap.put("1c", new OpCodeMath("INC E", 4, 1, OpCodeFunction.INC, OpCodeRegister.REGISTER_E));

		//DEC
		primaryOpCodeMap.put("05", new OpCodeMath("DEC B", 4, 1, OpCodeFunction.DEC, OpCodeRegister.REGISTER_B));
		primaryOpCodeMap.put("0d", new OpCodeMath("DEC C", 4, 1, OpCodeFunction.DEC, OpCodeRegister.REGISTER_C));
		primaryOpCodeMap.put("1d", new OpCodeMath("DEC E", 4, 1, OpCodeFunction.DEC, OpCodeRegister.REGISTER_E));
		primaryOpCodeMap.put("15", new OpCodeMath("DEC D", 4, 1, OpCodeFunction.DEC, OpCodeRegister.REGISTER_D));
		primaryOpCodeMap.put("3d", new OpCodeMath("DEC A", 4, 1, OpCodeFunction.DEC, OpCodeRegister.REGISTER_A));

		primaryOpCodeMap.put("fe", new OpCodeMath("CP d8", 8, 2, OpCodeFunction.CP));

		primaryOpCodeMap.put("d6", new OpCodeMath("SUB d8", 4, 2, OpCodeFunction.SUB));


		primaryOpCodeMap.put("0b", new OpCodeMath("DEC BC", 8, 1, OpCodeFunction.DEC_16, OpCodeRegister.REGISTERS_BC));
        primaryOpCodeMap.put("09", new OpCodeMath("ADD HL, BC", 8, 1, OpCodeFunction.ADD_16, OpCodeRegister.REGISTERS_HL, OpCodeRegister.REGISTERS_BC));
        primaryOpCodeMap.put("19", new OpCodeMath("ADD HL, DE", 8, 1, OpCodeFunction.ADD_16, OpCodeRegister.REGISTERS_HL, OpCodeRegister.REGISTERS_DE));
        primaryOpCodeMap.put("29", new OpCodeMath("ADD HL, HL", 8, 1, OpCodeFunction.ADD_16, OpCodeRegister.REGISTERS_HL, OpCodeRegister.REGISTERS_HL));
        primaryOpCodeMap.put("39", new OpCodeMath("ADD HL, SP", 8, 1, OpCodeFunction.ADD_16, OpCodeRegister.REGISTERS_HL, OpCodeRegister.REGISTERS_SP));


		//RLA
		primaryOpCodeMap.put("17", new OpCodeBit("RLA",4, 1, OpCodeBitFunction.RL, OpCodeRegister.REGISTER_A));
		//RLCA

		//CPL
		primaryOpCodeMap.put("2f", new OpCodeBit("CPL",4, 1, OpCodeBitFunction.CPL, OpCodeRegister.REGISTER_A));


		//C1-F1 pops, C5-F5 pushes to/from register to/from SP
		primaryOpCodeMap.put("c5", new OpCodeMath("PUSH BC", 16, 1, OpCodeFunction.PUSH, OpCodeRegister.REGISTERS_BC));
		primaryOpCodeMap.put("d5", new OpCodeMath("PUSH DE",16, 1, OpCodeFunction.PUSH, OpCodeRegister.REGISTERS_DE));
		primaryOpCodeMap.put("e5", new OpCodeMath("PUSH HL",16, 1, OpCodeFunction.PUSH, OpCodeRegister.REGISTERS_HL));
		primaryOpCodeMap.put("f5", new OpCodeMath("PUSH AF",16, 1, OpCodeFunction.PUSH, OpCodeRegister.REGISTERS_AF));

		primaryOpCodeMap.put("c1", new OpCodeMath("POP BC",12, 1, OpCodeFunction.POP, OpCodeRegister.REGISTERS_BC));
		primaryOpCodeMap.put("d1", new OpCodeMath("POP DE",12, 1, OpCodeFunction.POP, OpCodeRegister.REGISTERS_DE));
		primaryOpCodeMap.put("e1", new OpCodeMath("POP HL",12, 1, OpCodeFunction.POP, OpCodeRegister.REGISTERS_HL));
		primaryOpCodeMap.put("f1", new OpCodeMath("POP AF",12, 1, OpCodeFunction.POP, OpCodeRegister.REGISTERS_AF));




		generateCBPrefixCodes();
		generateLDCodes();
	}



    private void generateLDCodes() {


		//x6 and xE loading registers from data (8 codes)
		primaryOpCodeMap.put("0e", new OpCodeLD("LD C,d8",8, 2, OpCodeRegister.REGISTER_C));
		primaryOpCodeMap.put("06", new OpCodeLD("LD B,d8",8, 2, OpCodeRegister.REGISTER_B));
		primaryOpCodeMap.put("16", new OpCodeLD("LD D,d8",8, 2, OpCodeRegister.REGISTER_D));
		primaryOpCodeMap.put("3e", new OpCodeLD("LD A,d8",8, 2, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("2e", new OpCodeLD("LD L,d8",8, 2, OpCodeRegister.REGISTER_L));
		primaryOpCodeMap.put("1e", new OpCodeLD("LD E,d8",8, 2, OpCodeRegister.REGISTER_E));

		//xA loading A from address (4 codes)
        primaryOpCodeMap.put("0a", new OpCodeLD("LD A,(BC)",8, 1, OpCodeRegister.REGISTER_A, OpCodeRegister.ADDRESS_BC));
        primaryOpCodeMap.put("1a", new OpCodeLD("LD A,(DE)",8, 1, OpCodeRegister.REGISTER_A, OpCodeRegister.ADDRESS_DE));
        primaryOpCodeMap.put("2a", new OpCodeLD("LD A,(HL+)",8, 1, OpCodeRegister.REGISTER_A, OpCodeRegister.ADDRESS_HL_INC));
        primaryOpCodeMap.put("3a", new OpCodeLD("LD A,(HL-)",8, 1, OpCodeRegister.REGISTER_A, OpCodeRegister.ADDRESS_HL_DEC));

		//x2 loading A into address
        primaryOpCodeMap.put("02", new OpCodeLD("LD (BC),A", 8, 1, OpCodeRegister.ADDRESS_BC, OpCodeRegister.REGISTER_A));
        primaryOpCodeMap.put("12", new OpCodeLD("LD (DE),A", 8, 1, OpCodeRegister.ADDRESS_DE, OpCodeRegister.REGISTER_A));
        primaryOpCodeMap.put("22", new OpCodeLD("LD (HL+),A", 8, 1, OpCodeRegister.ADDRESS_HL_INC, OpCodeRegister.REGISTER_A));
        primaryOpCodeMap.put("32", new OpCodeLD("LD (HL-),A", 8, 1, OpCodeRegister.ADDRESS_HL_DEC, OpCodeRegister.REGISTER_A));


		//x1 loading 16bit registers
		primaryOpCodeMap.put("01", new OpCodeLD("LD BC,d16",12, 3, OpCodeRegister.REGISTERS_BC));
		primaryOpCodeMap.put("11", new OpCodeLD("LD DE,d16",12, 3, OpCodeRegister.REGISTERS_DE));
		primaryOpCodeMap.put("21", new OpCodeLD("LD HL,d16",12, 3, OpCodeRegister.REGISTERS_HL));
		primaryOpCodeMap.put("31", new OpCodeLD("LD SP,d16",12, 3, OpCodeRegister.REGISTERS_SP));


		primaryOpCodeMap.put("36", new OpCodeLD("LD (HL), d8",12, 2, OpCodeRegister.ADDRESS_HL));

		//4x-7x (minus 0x76 which is a halt) loading register into register and sometimes HL arithmetic
        String[] registerToRegisterOrder = {"B", "C", "D", "E", "H", "L", "(HL)", "A"};
        int startingHex = 0x40;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                String arg1 = registerToRegisterOrder[i];
                String arg2 = registerToRegisterOrder[j];
                int cycles = 4;
                if (arg1.equals("(HL)") || arg2.equals("(HL)")) {
                    cycles = 8;
                }
                if(startingHex == 0x76) {
                    startingHex++;
                    continue;
                }
                createOpCodeLD(Util.byteToHex(startingHex), "LD " + arg1+ "," + arg2, cycles, arg1, arg2 );
                startingHex++;
            }
        }

		primaryOpCodeMap.put("e0", new OpCodeLD("LDH ($FF00+a8), A",12, 2, OpCodeRegister.LDH_ADDRESS_FF00));
		primaryOpCodeMap.put("e2", new OpCodeLD("LD ($FF00+C), A",8, 1, OpCodeRegister.ADDRESS_FF00_C, OpCodeRegister.REGISTER_A));
		primaryOpCodeMap.put("ea", new OpCodeLD("LD (a16), A",16, 3, OpCodeRegister.A_TO_ADDRESS));
		primaryOpCodeMap.put("fa", new OpCodeLD("LD A,(a16)",16, 3, OpCodeRegister.ADDRESS_TO_A));
		primaryOpCodeMap.put("f0", new OpCodeLD("LDH A, ($FF00+a8)",12, 2, OpCodeRegister.LDH_ADDRESS_FF00_REGISTER_A));

	}

	private void generateCBPrefixCodes() {

        String[] functionOrder = {"RLC", "RRC", "RL", "RR", "SLA", "SRA", "SWAP", "SRL"};
        String[] registerToRegisterOrder = {"B", "C", "D", "E", "H", "L", "(HL)", "A"};
        int startingHex = 0x00;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                String arg1 = functionOrder[i];
                String arg2 = registerToRegisterOrder[j];
                int cycles = 8;
                if (arg2.equals("(HL)")) {
                    cycles = 16;
                }
                createOpCodeBit(Util.byteToHex(startingHex), arg1 + " " + arg2, cycles, arg1, arg2);
                startingHex++;
            }
        }

        startingHex = 0x40;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                String arg1 = "BIT";
                String arg2 = registerToRegisterOrder[j];
                int cycles = 8;
                if (arg2.equals("(HL)")) {
                    cycles = 16;
                }
                createOpCodeBit(Util.byteToHex(startingHex), arg1 + " " + i + "," + arg2, cycles, arg1, arg2, i);
                startingHex++;
            }
        }

        startingHex = 0x80;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                String arg1 = "RES";
                String arg2 = registerToRegisterOrder[j];
                int cycles = 8;
                if (arg2.equals("(HL)")) {
                    cycles = 16;
                }
                createOpCodeBit(Util.byteToHex(startingHex), arg1 + " " + i + "," + arg2, cycles, arg1, arg2, i);
                startingHex++;
            }
        }

        startingHex = 0xC0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                String arg1 = "SET";
                String arg2 = registerToRegisterOrder[j];
                int cycles = 8;
                if (arg2.equals("(HL)")) {
                    cycles = 16;
                }
                createOpCodeBit(Util.byteToHex(startingHex), arg1 + " " + i + "," + arg2, cycles, arg1, arg2, i);
                startingHex++;
            }
        }

	}



    public static OpCodeFactory getInstance() {
		if (singletonFactory == null) {
			singletonFactory = new OpCodeFactory();
		}
		return singletonFactory;
	}


	private void createOpCodeLD(String hexString, String doc, int cycles, String arg1, String arg2) {
        OpCodeRegister register1 = convertArgRegister(arg1);
        OpCodeRegister register2 = convertArgRegister(arg2);
        primaryOpCodeMap.put(hexString, new OpCodeLD(doc, cycles, 1, register1, register2));
    }

    private void createOpCodeLD(String hexString, String doc, int cycles, String arg1) {
        OpCodeRegister register1 = convertArgRegister(arg1);
        primaryOpCodeMap.put(hexString, new OpCodeLD(doc, cycles, 2, register1));

    }

    private void createOpCodeMath(String hexString, String doc, int cycles, String arg1, String arg2, boolean setResultToAccumulator) {
        OpCodeFunction function = convertArgFunction(arg1);
        OpCodeRegister register = convertArgRegister(arg2);
        if (setResultToAccumulator) {
            primaryOpCodeMap.put(hexString, new OpCodeMath(doc, cycles, 1, function, OpCodeRegister.REGISTER_A, register));
        }
        else {
            primaryOpCodeMap.put(hexString, new OpCodeMath(doc, cycles, 1, function, register));
        }

    }

    private void createOpCodeBit(String hexString, String doc, int cycles, String arg1, String arg2) {
        OpCodeBitFunction function = convertArgBitFunction(arg1);
        OpCodeRegister register = convertArgRegister(arg2);
        CBOpCodeMap.put(hexString, new OpCodeBit(doc, cycles, 2, function, register));
    }

    private void createOpCodeBit(String hexString, String doc, int cycles, String arg1, String arg2, int arg3) {
        OpCodeBitFunction function = convertArgBitFunction(arg1);
        OpCodeRegister register = convertArgRegister(arg2);
        CBOpCodeMap.put(hexString, new OpCodeBit(doc, cycles, 2, function, register, arg3));
    }

    private OpCodeRegister convertArgRegister(String register) {
	    return RegisterMap.get(register.toLowerCase());
    }

    private OpCodeFunction convertArgFunction(String register) {
        return FunctionMap.get(register.toLowerCase());
    }
    private OpCodeBitFunction convertArgBitFunction(String register) {
        return BitFunctionMap.get(register.toLowerCase());
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
	
	public int getOpCodeFromDocString(String docString) {
		for(Map.Entry<String, OpCode> entry : primaryOpCodeMap.entrySet()) {
			if(entry.getValue().toString().equals(docString)) {
				return Util.hexToByte(entry.getKey());
			}
		}
			
		return -1;
	}
}
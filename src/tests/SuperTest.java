package tests;

import static org.junit.Assert.*;
import main.Util;
import mmu.GameBoyMMU;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cpu.GameBoyCPU;
import cpu.OpCodeFactory;
import cpu.opcodetypes.OpCode;

public class SuperTest {

	public static GameBoyCPU cpu = GameBoyCPU.getInstance();
	public static GameBoyMMU mmu = GameBoyMMU.getInstance();
	public static OpCodeFactory fact = OpCodeFactory.getInstance();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mmu.initialize("test_tetris.gb", true);
	}

	@Before
	public void setUp() throws Exception {
		cpu.setF(0);
	}
	
	public void createAndRunOpCode(int opCodeNum)  {
		OpCode op = fact.constructOpCode(cpu.getProgramCounter(), opCodeNum);
		System.out.println(op.toString());
		try {
			cpu.setProgramCounter(cpu.getProgramCounter() + op.getInstructionSize());
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}
	
	public void createAndRunOpCode(String docString)  {
		int opCodeNum = fact.getOpCodeFromDocString(docString);
		OpCode op = fact.constructOpCode(cpu.getProgramCounter(), opCodeNum);
		System.out.println(op.toString());
		try {
			cpu.setProgramCounter(cpu.getProgramCounter() + op.getInstructionSize());
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	public void createAndRunOpCode8bitOperand(int opCodeNum, int operand)  {
		mmu.setMemoryAtAddress(cpu.getProgramCounter() + 1, operand);
		cpu.store8BitOperand();
		OpCode op = fact.constructOpCode(cpu.getProgramCounter(), opCodeNum);
		System.out.println(op.toString());
		try {
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void createAndRunOpCode8bitOperand(String docString, int operand)  {
		int opCodeNum = fact.getOpCodeFromDocString(docString);
		mmu.setMemoryAtAddress(cpu.getProgramCounter() + 1, operand);
		cpu.store8BitOperand();
		OpCode op = fact.constructOpCode(cpu.getProgramCounter(), opCodeNum);
		System.out.println(op.toString());
		try {
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void createAndRunOpCode16bitOperand(int opCodeNum, int operand, int operand2)  {
		mmu.setMemoryAtAddress(cpu.getProgramCounter() + 1, operand);
		mmu.setMemoryAtAddress(cpu.getProgramCounter() + 2, operand2);
		cpu.store16BitOperand();
		OpCode op = fact.constructOpCode(cpu.getProgramCounter(), opCodeNum);
		System.out.println(op.toString());
		try {
			cpu.setProgramCounter(cpu.getProgramCounter() + op.getInstructionSize());
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void createAndRunOpCode16bitOperand(String docString, int operand, int operand2)  {
		mmu.setMemoryAtAddress(cpu.getProgramCounter() + 1, operand);
		mmu.setMemoryAtAddress(cpu.getProgramCounter() + 2, operand2);
		cpu.store16BitOperand();
		int opCodeNum = fact.getOpCodeFromDocString(docString);
		OpCode op = fact.constructOpCode(cpu.getProgramCounter(), opCodeNum);
		System.out.println(op.toString());
		try {
			cpu.setProgramCounter(cpu.getProgramCounter() + op.getInstructionSize());
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void createAndRunCBOpCode(int opCodeNum)  {
		mmu.setMemoryAtAddress(cpu.getProgramCounter() + 1, opCodeNum);
		OpCode op = fact.constructOpCode(cpu.getProgramCounter(), 0xCB);
		System.out.println(op.toString());
		try {
			cpu.setProgramCounter(cpu.getProgramCounter() + op.getInstructionSize());
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	public void assertFlags(String zString, String nString, String hString, String cString) {
		boolean z,n,h,c;
		z = zString.equals("true") ? true : false;
		n = nString.equals("true") ? true : false;
		h = hString.equals("true") ? true : false;
		c = cString.equals("true") ? true : false;


		z = zString.equals("1") ? true : false;
		n = nString.equals("1") ? true : false;
		h = hString.equals("1") ? true : false;
		c = cString.equals("1") ? true : false;
		try {
			if(!zString.equals("-")) {
				assertEquals(cpu.getFlagZ(),z);
			}
			if(!nString.equals("-")) {
				assertEquals(cpu.getFlagN(), n);
			}
			if(!hString.equals("-")) {
				assertEquals(cpu.getFlagH(), h);
			}
			if(!cString.equals("-")) {
				assertEquals(cpu.getFlagC(), c);
			}
		}
		catch(Error e ) {
			fail(Util.flagsToString());
		}

	}

	public void assertFlags(int zi, int ni, int hi, int ci) {
		boolean z,n,h,c;
		z = (zi == 1)  ? true : false;
		n = (ni == 1) ? true : false;
		h = (hi == 1)  ? true : false;
		c = (ci == 1) ? true : false;


		try {
			if(zi != -1) {
				assertEquals(cpu.getFlagZ(),z);
			}
			if(ni != -1)  {
				assertEquals(cpu.getFlagN(), n);
			}
			if(hi != -1)  {
				assertEquals(cpu.getFlagH(), h);
			}
			if(ci != -1)  {
				assertEquals(cpu.getFlagC(), c);
			}
		}
		catch(Error e ) {
			fail(Util.flagsToString());
		}

	}

}

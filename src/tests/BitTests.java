package tests;

import cpu.GameBoyCPU;
import cpu.OpCodeFactory;
import cpu.opcodetypes.OpCode;
import main.Util;
import mmu.GameBoyMMU;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BitTests {

	public static GameBoyCPU cpu = GameBoyCPU.getInstance();
	public static GameBoyMMU mmu = GameBoyMMU.getInstance();
	public static OpCodeFactory fact = OpCodeFactory.getInstance();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mmu.initialize("test_tetris.gb");
		mmu.disableBootrom = true;
	}

	@Before
	public void setUp() throws Exception {
		cpu.setF(0);
	}


	private void createAndRunOpCode(int opCodeNum)  {
		OpCode op = fact.constructOpCode(0, opCodeNum);
		System.out.println(op.toString());
		try {
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	private void createAndRunOpCode8bitOperand(int opCodeNum, int operand)  {
		mmu.setMemoryAtAddress(1, operand);
		OpCode op = fact.constructOpCode(0, opCodeNum);
		System.out.println(op.toString());
		try {
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private void createAndRunOpCode16bitOperand(int opCodeNum, int operand, int operand2)  {
		mmu.setMemoryAtAddress(1, operand);
		mmu.setMemoryAtAddress(2, operand2);
		OpCode op = fact.constructOpCode(0, opCodeNum);
		System.out.println(op.toString());
		try {
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private void createAndRunCBOpCode(int opCodeNum)  {
		mmu.setMemoryAtAddress(1, opCodeNum);
		OpCode op = fact.constructOpCode(0, 0xCB);
		System.out.println(op.toString());
		try {
			op.runCode(cpu, mmu);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	private void assertFlags(String zString, String nString, String hString, String cString) {
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

	private void assertFlags(int zi, int ni, int hi, int ci) {
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

	@Test
	public void testRL_registerC() {
		cpu.setC(0b10000001 & 0xFF);
		cpu.setF(0);
		createAndRunCBOpCode(0x11);
		assertEquals(cpu.getC(),0b00000010 & 0xFF);
		assertFlags(0,0,0,1);

		cpu.setC(0b10000001 & 0xFF);
		cpu.setF(0b00010000 & 0xFF);
		createAndRunCBOpCode(0x11);
		assertEquals(cpu.getC(),0b00000011 & 0xFF);
		assertFlags(0,0,0,1);

		cpu.setC(0b00000001 & 0xFF);
		cpu.setF(0b00010000 & 0xFF);
		createAndRunCBOpCode(0x11);
		assertEquals(cpu.getC(),0b00000011 & 0xFF);
		assertFlags(0,0,0,0);

		cpu.setC(0b10000000 & 0xFF);
		cpu.setF(0);
		createAndRunCBOpCode(0x11);
		assertEquals(cpu.getC(),0);
		assertFlags(1,0,0,1);
		
	}

	@Test
	public void testRLA() {
		cpu.setA(0b10000001 & 0xFF);
		createAndRunOpCode(0x17);
		assertEquals(cpu.getA(),0b00000010 & 0xFF);
		assertFlags(0,0,0,1);

		cpu.setA(0b10000001 & 0xFF);
		cpu.setF(0b00010000 & 0xFF);
		createAndRunOpCode(0x17);
		assertEquals(cpu.getA(),0b00000011 & 0xFF);
		assertFlags(0,0,0,1);

		cpu.setA(0b10000000 & 0xFF);
		cpu.setF(0);
		createAndRunOpCode(0x17);
		assertEquals(cpu.getA(),0);
		assertFlags(1,0,0,1);

		cpu.setA(0b00000001 & 0xFF);
		cpu.setF(0);
		createAndRunOpCode(0x17);
		assertEquals(cpu.getA(),0b00000010);
		assertFlags(0,0,0,0);
	}

	@Test
	public void testCP() {
		cpu.setA(0b11111111 & 0xFF);
		cpu.setF(0);
		createAndRunOpCode8bitOperand(0xfe, 0b10011001 & 0xFF);
		assertEquals(cpu.getA(), 0b11111111 & 0xFF);
		assertFlags(0,1,0,0);

	}

	@Test
	public void testSUB() {
		cpu.setA(0b11111111 & 0xFF);
		cpu.setF(0);
		createAndRunOpCode8bitOperand(0x90, 0b10011001 & 0xFF);
		assertEquals(cpu.getA(), 0b11111111 & 0xFF);
		assertFlags(0,1,0,0);

	}

}

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

public class BitTests extends SuperTest {


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

	@Test
	public void testCPL() {
		cpu.setA(0b01010101 & 0xFF);
		cpu.setF(0);
		createAndRunOpCode(0x2f);
		assertEquals(cpu.getA(), 0b10101010 & 0xFF);
	}
	
	@Test
	public void testBit() {
		cpu.setA(0b01010101 & 0xFF);
		cpu.setF(0);
		createAndRunCBOpCode(0x47);
		assertFlags(0,0,1,-1);

		cpu.setA(0b11010100 & 0xFF);
		cpu.setF(0);
		createAndRunCBOpCode(0x47);
		assertFlags(1,0,1,-1);
		

		cpu.setA(0b01010101 & 0xFF);
		cpu.setF(0);
		createAndRunCBOpCode(0x47);
		assertFlags(0,0,1,-1);

		cpu.setA(0b11010100 & 0xFF);
		cpu.setF(0);
		createAndRunCBOpCode(0x77);
		assertFlags(0,0,1,-1);
	}

}

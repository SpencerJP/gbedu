package tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class JumpTests extends SuperTest {

	

	@Test
	public void testJumpToAddress() {
		cpu.setProgramCounter(0x100);
		createAndRunOpCode16bitOperand(0xc3, 0x00, 0x00); // JP a16
		assertEquals(cpu.getProgramCounter(), 0x00);

		cpu.setProgramCounter(0x100);
		createAndRunOpCode16bitOperand(0xc3, 0xfe, 0xff); // JP a16
		assertEquals(cpu.getProgramCounter(), 0xfffe);
		
		cpu.setHL(0x4abc);
		createAndRunOpCode(0xe9); // JP (HL)
		assertEquals(cpu.getProgramCounter(), 0x4abc);
		
	}
	
	@Test
	public void testAddToAddress() {
		cpu.setProgramCounter(0xA + 2);
		createAndRunOpCode8bitOperand(0x18, 0xfb); // JR d8
		assertEquals(cpu.getProgramCounter(), 0x7);
		
		cpu.setFlagZ(false);
		cpu.setProgramCounter(0xA + 2);
		createAndRunOpCode8bitOperand(0x20, 0xfb); // JR NZ d8
		assertEquals(cpu.getProgramCounter(), 0x7);
		

		cpu.setFlagZ(true);
		cpu.setProgramCounter(0xA + 2);
		createAndRunOpCode8bitOperand(0x20, 0xfb); // JR NZ d8
		assertEquals(cpu.getProgramCounter(), 0xA + 2);
		
		
		cpu.setFlagC(false);
		cpu.setProgramCounter(0xA + 2);
		createAndRunOpCode8bitOperand(0x30, 0xfb); // JR NZ d8
		assertEquals(cpu.getProgramCounter(), 0x7);
		

		cpu.setFlagC(true);
		cpu.setProgramCounter(0xA + 2);
		createAndRunOpCode8bitOperand(0x30, 0xfb); // JR NZ d8
		assertEquals(cpu.getProgramCounter(), 0xA + 2);
	}

	
	@Test
	public void testCalls() {
		cpu.setProgramCounter(0x100);
		createAndRunOpCode16bitOperand(0xcd, 0xfe, 0xff);
		assertEquals(cpu.getProgramCounter(), 0xfffe);
		assertEquals(cpu.popSP(), 0x100 + 3);
	}
	
	@Test
	public void testReturns() {

		cpu.setProgramCounter(0xfffe);
		cpu.pushSP(0x100);
		createAndRunOpCode(0xc9);
		assertEquals(cpu.getProgramCounter(), 0x100);

		cpu.setProgramCounter(0xfffe);
		cpu.pushSP(0x100);
		cpu.pushSP(0x150);
		createAndRunOpCode(0xc9);
		assertEquals(cpu.getProgramCounter(), 0x150);
		
	}
	
	@Test
	public void testCallAndReturn() {
		cpu.setProgramCounter(0x100);
		createAndRunOpCode16bitOperand(0xcd, 0xfe, 0xff);
		createAndRunOpCode(0xc9);
		assertEquals(cpu.getProgramCounter(), 0x100 + 3);
	}
	
	@Test
	public void testRestarts() {
		cpu.setProgramCounter(0x4000);
		createAndRunOpCode(0xef);
		assertEquals(cpu.getProgramCounter(), 0x28);
		
	}
}

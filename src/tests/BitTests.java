package tests;

import static org.junit.Assert.*;
import mmu.GameBoyMMU;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cpu.GameBoyCPU;
import cpu.OpCodeFactory;

public class BitTests {

	public static GameBoyCPU cpu = GameBoyCPU.getInstance();
	public static GameBoyMMU mmu = GameBoyMMU.getInstance();
	public static OpCodeFactory fact = OpCodeFactory.getInstance();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mmu.initialize("test_tetris.gb");
	}
	

	@Test
	public void testRL_registerC() {
		cpu.setC(0b10000001 & 0xFF);
		fact.constructOpCode(0, 0x11);
		assertEquals(cpu.getC(),0b00000010 & 0xFF);
		cpu.setC(0b10000001 & 0xFF);
		cpu.setF(0b00010000 & 0xFF);
		fact.constructOpCode(0, 0x11);
		assertEquals(cpu.getC(),0b00000011 & 0xFF);
		
	}

}

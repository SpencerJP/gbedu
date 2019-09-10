package tests;

import static org.junit.Assert.*;

import org.junit.Test;

public class MathTests extends SuperTest {

	@Test
	public void testAdd_16() { // 19 hl = hl + de
		cpu.setHL(55);
		cpu.setDE(55);
		cpu.setF(0);
		createAndRunOpCode(0x19);
		assertEquals(cpu.getHL(), 110);
		assertEquals(cpu.getDE(), 55);
		assertFlags(-1,0,-1,-1);
		

		cpu.setHL(0x44);
		cpu.setDE(0xffff);
		cpu.setF(0);
		createAndRunOpCode(0x19);
		assertEquals(cpu.getHL(), 0x43);
		assertEquals(cpu.getDE(), 0xffff);
		assertFlags(-1,0,-1,1);
		
		
	}
	
	@Test
	public void testXOR() {
		cpu.setF(0);
		cpu.setA(0b11001100);
		cpu.setC(0b11001100);
		createAndRunOpCode("XOR C");
		assertEquals(cpu.getA(), 0);
		

		cpu.setF(0);
		cpu.setA(0b11001100);
		cpu.setC(0b00110011);
		createAndRunOpCode("XOR C");
		assertEquals(cpu.getA(), 0b11111111);
	}
	
	@Test
	public void testSBC() {
		cpu.setF(0b00010000);
		cpu.setA(0b11111111);
		cpu.setC(0b11111111);
		createAndRunOpCode("SBC A,C");
		System.out.println(cpu.getA()+"");
	}
	
	@Test
	public void testSUB() {
		cpu.setF(0b00010000);
		cpu.setA(0b11111111);
		cpu.setC(0b11111111);
		createAndRunOpCode("SUB C");
		System.out.println(cpu.getA()+"");
	}


}

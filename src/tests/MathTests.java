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

}

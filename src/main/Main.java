package main;

import cpu.GameBoyCPU;
import mmu.GameBoyMMU;

public class Main {
	public static void main(String[] args) {
		int i = 251;
		byte b = (byte) i;
		System.out.println(b);
		try {
			GameBoyMMU memory = GameBoyMMU.getInstance();
			memory.initialize("DMG_ROM.bin");
			//memory.dump();
			GameBoyCPU cpu = GameBoyCPU.getInstance();
			cpu.run();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package main;

import cpu.GameBoyCPU;
import mmu.GameBoyMMU;

public class Main {
	public static void main(String[] args) {
		try {
			GameBoyMMU memory = GameBoyMMU.getInstance();
			memory.initialize("DMG_ROM.bin");
			System.out.println(Utility.encodeHexString(memory.getMemory()));
			GameBoyCPU cpu = GameBoyCPU.getInstance();
			cpu.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

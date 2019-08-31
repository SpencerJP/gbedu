package main;

import cpu.GameBoyCPU;
import mmu.GameBoyMMU;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		GameBoyMMU memory = GameBoyMMU.getInstance();
		try {
			memory.initialize("DMG_ROM.bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//memory.dump();
		GameBoyCPU cpu = GameBoyCPU.getInstance();
		try {
			cpu.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

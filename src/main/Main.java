package main;

import cpu.GameBoyCPU;
import gpu.GameBoyGPU;
import mmu.GameBoyMMU;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		GameBoyMMU memory = GameBoyMMU.getInstance();
		GameBoyGPU gpu = GameBoyGPU.getInstance();
        GameBoyCPU cpu = GameBoyCPU.getInstance();
		try {
			memory.initialize("test_tetris.gb");

			//System.out.println(memory.getMemoryAtAddress(0x104));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//memory.dump();
		try {
			cpu.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

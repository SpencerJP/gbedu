package main;

import cpu.GameBoyCPU;
import gpu.GameBoyGPU;
import mmu.GameBoyMMU;

import java.io.IOException;
import java.util.logging.Level;

public class Main {
	public static void main(String[] args) {
		Util.isDebugMode = false;
		boolean disableBootrom = true;
		GameBoyMMU memory = GameBoyMMU.getInstance();
		@SuppressWarnings("unused")
		GameBoyGPU gpu = GameBoyGPU.getInstance();
        GameBoyCPU cpu = GameBoyCPU.getInstance();
		long start = System.currentTimeMillis();
		try {
			memory.initialize("test_tetris.gb", disableBootrom);
//			memory.initialize("cpu_instrs.gb", disableBootrom);
//			memory.initialize("01-special.gb", true);
//			memory.initialize("drmarioworld.gb", disableBootrom);
//			memory.initialize("opus5.gb", disableBootrom);
//			memory.initialize("03-op sp,hl.gb", true);

			//System.out.println(memory.getMemoryAtAddress(0x104));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//memory.dump();
		try {
			cpu.run();
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			Util.log(Level.SEVERE, end - start + "");
			e.printStackTrace();
		}
	}
}

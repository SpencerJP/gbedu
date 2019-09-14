package tests;

import cpu.GameBoyCPU;
import cpu.OpCodeFactory;
import gpu.GameBoyGPU;
import main.Util;
import mmu.GameBoyMMU;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Level;

public class BlargTests {

    public static GameBoyCPU cpu = GameBoyCPU.getInstance();
    public static GameBoyMMU mmu = GameBoyMMU.getInstance();
    public static OpCodeFactory fact = OpCodeFactory.getInstance();

    @Test
    public void testRom() throws IOException {
        Util.isDebugMode = false;
        @SuppressWarnings("unused")
        boolean disableBootrom = false;
        GameBoyMMU memory = GameBoyMMU.getInstance();
        @SuppressWarnings("unused")
        GameBoyGPU gpu = GameBoyGPU.getInstance();
        @SuppressWarnings("unused")
        GameBoyCPU cpu = GameBoyCPU.getInstance();
//        memory.initialize("01-special.gb", true);
//        memory.initialize("02-interrupts.gb", true);
//        memory.initialize("03-op sp,hl.gb", true);
//        memory.initialize("04-op r,imm.gb", true);
//        memory.initialize("05-op rp.gb", true);
//        memory.initialize("06-ld r,r.gb", true);
        memory.initialize("07-jr,jp,call,ret,rst.gb", true);
//        memory.initialize("08-misc instrs.gb", true);
//        memory.initialize("09-op r,r.gb", true);
//        memory.initialize("10-bit ops.gb", true);
//        memory.initialize("11-op a,(hl).gb", true);
    }


}

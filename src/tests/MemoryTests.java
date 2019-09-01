package tests;

import cpu.GameBoyCPU;
import mmu.GameBoyMMU;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.fail;

public class MemoryTests {

    GameBoyMMU mmu;
    @BeforeClass
    public void setUp() {
        mmu = GameBoyMMU.getInstance();
        try {
            mmu.initialize("test_tetris.gb");
        } catch (IOException e) {
            fail("Tetris rom or bootrom doesn't exist");
        }
    }
    @Test
    public void checkDataPoints() {
    }
}

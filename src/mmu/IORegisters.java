package mmu;

public class IORegisters {

    public static final int BOOTROM_STATUS = 0xff50;

    public static GameBoyMMU mmu;

    public IORegisters(GameBoyMMU mmu) {
        this.mmu = mmu;
    }

    public static boolean getBootromEnabled() {
        return (mmu.getMemoryAtAddress(BOOTROM_STATUS) == 0x01);
    }
}

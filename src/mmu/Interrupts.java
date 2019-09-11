package mmu;

import main.Util;

public class Interrupts {
	
	public static int INTERRUPT_ENABLED = 0xffff;
	public static int INTERRUPT_FLAGS = 0xff0f;
	
	public static int VBLANK_BITPOS = 0;
	public static int LCDSTAT_BITPOS = 1;
	public static int TIMER_BITPOS = 2;
	public static int SERIAL_BITPOS = 3;
	public static int JOYPAD_BITPOS = 4;
	
	public static boolean isVblankInterruptEnabled() {
		return Util.getBit(VBLANK_BITPOS ,Util.getMemory().getMemoryAtAddress(INTERRUPT_ENABLED));
	}
	
	public static boolean isLCDStatInterruptEnabled() {
		return Util.getBit(LCDSTAT_BITPOS ,Util.getMemory().getMemoryAtAddress(INTERRUPT_ENABLED));
	}
	
	public static boolean isTimerInterruptEnabled() {
		return Util.getBit(TIMER_BITPOS ,Util.getMemory().getMemoryAtAddress(INTERRUPT_ENABLED));
	}

	public static boolean isSerialInterruptEnabled() {
		return Util.getBit(SERIAL_BITPOS ,Util.getMemory().getMemoryAtAddress(INTERRUPT_ENABLED));
	}
	

	public static boolean isJoypadInterruptEnabled() {
		return Util.getBit(JOYPAD_BITPOS ,Util.getMemory().getMemoryAtAddress(INTERRUPT_ENABLED));
	}
	
	public static boolean hasVblankInterruptOccurred() {
		return Util.getBit(VBLANK_BITPOS ,Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS));
	}
	
	public static boolean hasLCDStatInterruptOccurred() {
		return Util.getBit(LCDSTAT_BITPOS ,Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS));
	}
	
	public static boolean hasTimerInterruptOccurred() {
		return Util.getBit(TIMER_BITPOS ,Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS));
	}

	public static boolean hasSerialInterruptOccurred() {
		return Util.getBit(SERIAL_BITPOS,Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS));
	}
	

	public static boolean hasJoypadInterruptOccurred() {
		return Util.getBit(JOYPAD_BITPOS ,Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS));
	}
	
	public static void resetVblankInterrupt() {
		int current = Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS);
		Util.getMemory().setMemoryAtAddress(INTERRUPT_FLAGS, Util.resetBit(current, VBLANK_BITPOS));
	}
	
	public static void resetLCDStatInterrupt() {
		int current = Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS);
		Util.getMemory().setMemoryAtAddress(INTERRUPT_FLAGS, Util.resetBit(current, LCDSTAT_BITPOS));	
	}
	
	public static void resetTimerInterrupt() {
		int current = Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS);
		Util.getMemory().setMemoryAtAddress(INTERRUPT_FLAGS, Util.resetBit(current, TIMER_BITPOS));
	}
	
	public static void resetSerialInterrupt() {
		int current = Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS);
		Util.getMemory().setMemoryAtAddress(INTERRUPT_FLAGS, Util.resetBit(current, SERIAL_BITPOS));	
	}
	
	public static void resetJoypadInterrupt() {
		int current = Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS);
		Util.getMemory().setMemoryAtAddress(INTERRUPT_FLAGS, Util.resetBit(current, JOYPAD_BITPOS));
	}
	

	public static void setVblankInterrupt() {
		int current = Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS);
		Util.getMemory().setMemoryAtAddress(INTERRUPT_FLAGS, Util.setBit(current, VBLANK_BITPOS));
	}
	
	public static void setLCDStatInterrupt() {
		int current = Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS);
		Util.getMemory().setMemoryAtAddress(INTERRUPT_FLAGS, Util.setBit(current, LCDSTAT_BITPOS));	
	}
	
	public static void setTimerInterrupt() {
		int current = Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS);
		Util.getMemory().setMemoryAtAddress(INTERRUPT_FLAGS, Util.setBit(current, TIMER_BITPOS));
	}
	
	public static void setSerialInterrupt() {
		int current = Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS);
		Util.getMemory().setMemoryAtAddress(INTERRUPT_FLAGS, Util.setBit(current, SERIAL_BITPOS));	
	}
	
	public static void setJoypadInterrupt() {
		int current = Util.getMemory().getMemoryAtAddress(INTERRUPT_FLAGS);
		Util.getMemory().setMemoryAtAddress(INTERRUPT_FLAGS, Util.setBit(current, JOYPAD_BITPOS));
		
	}
}

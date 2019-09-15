package mmu;

import main.Util;

public class Interrupts {
	
	public static final int INTERRUPT_ENABLED = 0xFFFF;
	public static final int INTERRUPT_FLAGS = 0xFF0F;
	
	public static final int VBLANK_BITPOS = 0;
	public static final int LCDSTAT_BITPOS = 1;
	public static final int TIMER_BITPOS = 2;
	public static final int SERIAL_BITPOS = 3;
	public static final int JOYPAD_BITPOS = 4;

	public int interruptsEnabledRegister = 0;
	public int interruptFlagsRegister = 0;

	public int getRegisters(int address) {
		if(address == INTERRUPT_ENABLED) {
			return interruptsEnabledRegister;
		}
		if(address == INTERRUPT_FLAGS) {
			return interruptFlagsRegister;
		}
		throw new RuntimeException("Interrupts: Shouldn't happen");

	}

	public void setRegisters(int address, int source) {
		if(address == INTERRUPT_ENABLED) {
			interruptsEnabledRegister = source & 0xff;
		}
		if(address == INTERRUPT_FLAGS) {
			interruptFlagsRegister = source & 0xff;
		}

	}

	public boolean isVblankInterruptEnabled() {
		return Util.getBit(interruptsEnabledRegister, VBLANK_BITPOS);
	}
	
	public boolean isLCDStatInterruptEnabled() {
		return Util.getBit(interruptsEnabledRegister, LCDSTAT_BITPOS);
	}
	
	public boolean isTimerInterruptEnabled() {
		return Util.getBit(interruptsEnabledRegister, TIMER_BITPOS);
	}

	public boolean isSerialInterruptEnabled() {
		return Util.getBit(interruptsEnabledRegister, SERIAL_BITPOS);
	}

	public boolean isJoypadInterruptEnabled() {
		return Util.getBit(interruptsEnabledRegister, JOYPAD_BITPOS);
	}
	
	public boolean hasVblankInterruptOccurred() {
		return Util.getBit(interruptFlagsRegister, VBLANK_BITPOS);
	}
	
	public boolean hasLCDStatInterruptOccurred() {
		return Util.getBit(interruptFlagsRegister, LCDSTAT_BITPOS);
	}
	
	public boolean hasTimerInterruptOccurred() {
		return Util.getBit(interruptFlagsRegister, TIMER_BITPOS);
	}

	public boolean hasSerialInterruptOccurred() {
		return Util.getBit(interruptFlagsRegister, SERIAL_BITPOS);
	}
	

	public boolean hasJoypadInterruptOccurred() {
		return Util.getBit(interruptFlagsRegister, JOYPAD_BITPOS);
	}
	
	public void resetVblankInterrupt() {
		int current = interruptFlagsRegister;
		interruptFlagsRegister = Util.resetBit(current, VBLANK_BITPOS);
	}
	
	public void resetLCDStatInterrupt() {
		int current = interruptFlagsRegister;
		interruptFlagsRegister = Util.resetBit(current, LCDSTAT_BITPOS);
	}
	
	public void resetTimerInterrupt() {
		int current = interruptFlagsRegister;
		interruptFlagsRegister = Util.resetBit(current, TIMER_BITPOS);
	}
	
	public void resetSerialInterrupt() {
		int current = interruptFlagsRegister;
		interruptFlagsRegister = Util.resetBit(current, SERIAL_BITPOS);
	}
	
	public void resetJoypadInterrupt() {
		int current = interruptFlagsRegister;
		interruptFlagsRegister = Util.resetBit(current, JOYPAD_BITPOS);
	}
	

	public void setVblankInterrupt() {
		int current = interruptFlagsRegister;
		interruptFlagsRegister = Util.setBit(current, VBLANK_BITPOS);
	}
	
	public void setLCDStatInterrupt() {
		int current = interruptFlagsRegister;
		interruptFlagsRegister = Util.setBit(current, LCDSTAT_BITPOS);
	}
	
	public void setTimerInterrupt() {
		int current = interruptFlagsRegister;
		interruptFlagsRegister = Util.setBit(current, TIMER_BITPOS);
	}
	
	public void setSerialInterrupt() {
		int current = interruptFlagsRegister;
		interruptFlagsRegister = Util.setBit(current, SERIAL_BITPOS);
	}
	
	public void setJoypadInterrupt() {
		int current = interruptFlagsRegister;
		interruptFlagsRegister = Util.setBit(current, JOYPAD_BITPOS);
	}
}

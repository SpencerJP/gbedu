package cpu;

import cpu.opcodetypes.MissingOpCodeException;
import cpu.opcodetypes.OpCode;
import gpu.GameBoyGPU;
import main.Util;
import mmu.GameBoyMMU;
import mmu.Interrupts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

public class GameBoyCPU {

	private static final double CPU_CLOCK_RATE = 4.1934;
	GameBoyMMU mmu;
	OpCodeFactory opFact;


	private int a = 0x00; // accumulator
	private int b = 0x00;
	private int c = 0x00;
	private int d = 0x00;
	private int e = 0x00;
	private int h = 0x00;
	private int l = 0x00;
	private int f = 0x00; // flag register
	private int s = 0x00;
	private int p = 0x00;

	public ArrayList<Integer> programPositionsOnce = new ArrayList<Integer>();
	public boolean setOnce = false; // todo delete
	private int bit8Operand = 0;
	private int bit16Operand = 0;

	private boolean interruptsEnabled = false;

	private int programCounter = 0;


	
	
	private static GameBoyCPU singletonInstance;
    private int clockTime;

    private GameBoyCPU() {
		this.mmu = GameBoyMMU.getInstance();
		this.opFact = OpCodeFactory.getInstance();
	}
	
	public static GameBoyCPU getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new GameBoyCPU();
		}
		return singletonInstance;
	}
	
	
	
	public void run() throws Exception {
		int cycles = 0;
		GameBoyGPU gpu = GameBoyGPU.getInstance();

		//GameBoyMMU.addAddressToWatchlist(0xff40);
		
		while(true) {
			cycles = runOperation();
			Clock.addClockTime(cycles);
			if (interruptsEnabled) {
				checkInterrupts();
			}
			
			if (clockTime >= 4194*2) {
                clockTime = 0;
                Thread.sleep(1);
			}

//			if (programCounter == 0x0384) {
//					int i = 0;
//			}
		}
	}
	


	private int runOperation() throws Exception {
		int cycles = 0;
		try {
			int opCodeNum = mmu.getMemoryAtAddress(getProgramCounter());
			OpCode op = opFact.constructOpCode(getProgramCounter(), opCodeNum);

			if (op == null) {
				throw new MissingOpCodeException(mmu, getProgramCounter());
			}
			if(op.getInstructionSize() == 2 && opCodeNum != 0xcb) {
				store8BitOperand();
				Util.log("next opCode: " + Util.byteToHex(mmu.getMemoryAtAddress(getProgramCounter())) + "["+op.toString()+"], operand: " + Util.byteToHex(get8BitOperand()) + " at position " + getProgramCounter() + " (0x" + Util.byteToHex16(getProgramCounter()) + ")");
				if(!programPositionsOnce.contains(programCounter)) {
					programPositionsOnce.add(programCounter);
					if(Util.getLogger().getLevel() != Level.INFO) {
						System.out.println("PC: " + Util.byteToHex16(programCounter) + " -> Running " + op.toString() + " with operand " + Util.byteToHex(get8BitOperand()));
					}

				}
			}
			else if(op.getInstructionSize() == 3) {
				store16BitOperand();
				Util.log("next opCode: " + Util.byteToHex(mmu.getMemoryAtAddress(getProgramCounter())) + "["+op.toString()+"], operand: " + Util.byteToHex16(get16BitOperand()) + " at position " + getProgramCounter() + " (0x" + Util.byteToHex16(getProgramCounter()) + ")");
				if(!programPositionsOnce.contains(programCounter)) {
					programPositionsOnce.add(programCounter);
					if(Util.getLogger().getLevel() != Level.INFO) {
						System.out.println("PC: " + Util.byteToHex16(programCounter) + " -> Running " + op.toString() + " with operand " + Util.byteToHex16(get16BitOperand()));
					}
				}
			}
			else {
				Util.log("next opCode: " + Util.byteToHex(mmu.getMemoryAtAddress(getProgramCounter())) + "["+op.toString()+"] at position " + getProgramCounter() + " (0x" + Util.byteToHex16(getProgramCounter()) + ")");
				if(!programPositionsOnce.contains(programCounter)) {
					programPositionsOnce.add(programCounter);
					if(Util.getLogger().getLevel() != Level.INFO) {
						System.out.println("PC: " + Util.byteToHex16(programCounter) + " -> Running " + op.toString());
					}
				}
			}
			setProgramCounter(getProgramCounter() + op.getInstructionSize());


			cycles = op.runCode(this, mmu);
		}
		catch(MissingOpCodeException e) {
			Util.log(Level.SEVERE, e.getMessage());
			throw new Exception();
		}
		catch(UnsupportedOperationException e){

			Util.log(Level.SEVERE, e.getMessage());
			throw new UnsupportedOperationException(e.getMessage());
		}
		return cycles;
		
	}
	

	private void checkInterrupts() {
		if(Interrupts.isVblankInterruptEnabled() && Interrupts.hasVblankInterruptOccurred()) {
			Interrupts.resetVblankInterrupt();
			setInterruptsEnabled(false);
			pushSP(programCounter);
			setProgramCounter(0x40);
			return;
		}
		
		if(Interrupts.isLCDStatInterruptEnabled() && Interrupts.hasLCDStatInterruptOccurred()) {
			Interrupts.resetLCDStatInterrupt();
			setInterruptsEnabled(false);
			pushSP(programCounter);
			setProgramCounter(0x48);
			return;
		}
		
		if(Interrupts.isTimerInterruptEnabled() && Interrupts.hasTimerInterruptOccurred()) {
			Interrupts.resetTimerInterrupt();
			setInterruptsEnabled(false);
			pushSP(programCounter);
			setProgramCounter(0x50);
			return;
		}
		
		if(Interrupts.isSerialInterruptEnabled() && Interrupts.hasSerialInterruptOccurred()) {
			Interrupts.resetSerialInterrupt();
			setInterruptsEnabled(false);
			pushSP(programCounter);
			setProgramCounter(0x58);
			return;
		}
		
		if(Interrupts.isJoypadInterruptEnabled() && Interrupts.hasJoypadInterruptOccurred()) {
			Interrupts.resetJoypadInterrupt();
			setInterruptsEnabled(false);
			pushSP(programCounter);
			setProgramCounter(0x60);
		}
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public int getE() {
		return e;
	}

	public void setE(int e) {
		this.e = e;
	}

	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}

	public void setS(int s) {
		this.s = s;
	}

	public void setP(int p) {
		this.p = p;
	}

	public int getProgramCounter() {
		return programCounter;
	}

	public void setProgramCounter(int newAddress) {
		this.programCounter = newAddress;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public void setHL(int data) {
		setL((data & 0xff));
		setH((data >> 8) & 0xff);
	}

	public void setSP(int data) {
		setP((data & 0xff));
		setS((data >> 8) & 0xff);
		
	}

	public void setBC(int data) {
		setC((data & 0xff));
		setB((data >> 8) & 0xff);
		
	}
	public void setDE(int data) {
		setE((data & 0xff));
		setD((data >> 8) & 0xff);
		
	}

	public void setAF(int data) {
		setF((data & 0xff));
		setA((data >> 8) & 0xff);

	}

	public int getHL() {
		return (h << 8 | l);
				
	}
	
	public int getSP() {
		return (s << 8 | p);
				
	}
	
	public int getDE() {
		return (d << 8 | e);
				
	}
	
	public int getBC() {
		return (b << 8 | c);
				
	}

	public int getAF() {
		return (a << 8 | f);
	}

	/**
	 *
	 * @param data in sixteen bits
	 */
	public void pushSP(int data) {
		int leftByte = (byte)(data & 0xff);
		int rightByte = (byte)((data >> 8) & 0xFF);
		setSP(getSP() - 1);
		Util.getMemory().setMemoryAtAddress(getSP(), leftByte);
		setSP(getSP() - 1);
		Util.getMemory().setMemoryAtAddress(getSP(), rightByte);
	}

	public int popSP() {
		int leftByte = Util.getMemory().getMemoryAtAddress(getSP());
		setSP(getSP() + 1);
		int rightByte = Util.getMemory().getMemoryAtAddress(getSP());
		setSP(getSP() + 1);
		return (leftByte << 8 | rightByte);
	}

	public void setFlagC(boolean set) {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		if(set) {
			cpu.setF(Util.setBit(f, 4));
		}
		else {
			cpu.setF(Util.resetBit(f, 4));
		}
	}
	public void setFlagH(boolean set) {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		if(set) {
			cpu.setF(Util.setBit(f, 5));
		}
		else {
			cpu.setF(Util.resetBit(f, 5));
		}
	}
	public void setFlagN(boolean set) {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		if(set) {
			cpu.setF(Util.setBit(f, 6));
		}
		else {
			cpu.setF(Util.resetBit(f, 6));
		}
	}
	public void setFlagZ(boolean set) {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		if(set) {
			cpu.setF(Util.setBit(f, 7));
		}
		else {
			cpu.setF(Util.resetBit(f, 7));
		}
	}

	public boolean getFlagC() {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		return Util.getBit(f, 4);
	}
	public boolean getFlagH() {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		return Util.getBit(f, 5);
	}

	public boolean getFlagN() {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		return Util.getBit(f, 6);
	}

	public boolean getFlagZ() {
		GameBoyCPU cpu = Util.getCPU();
		int f = cpu.getF();
		return Util.getBit(f, 7);
	}


	public int get8BitOperand() {
		return bit8Operand;
	}

	public int get16BitOperand() {
		return bit16Operand;
	}

	public void store8BitOperand() {
		bit8Operand = Util.getMemory().getMemoryAtAddress(programCounter + 1);
	}

	public void store16BitOperand() {
		int leftHalf = Util.getMemory().getMemoryAtAddress(programCounter + 2);
		int rightHalf = Util.getMemory().getMemoryAtAddress(programCounter + 1);
		bit16Operand = ((leftHalf << 8) | rightHalf);
	}

	public boolean isInterruptsEnabled() {
		return interruptsEnabled;
	}

	public void setInterruptsEnabled(boolean interruptsEnabled) {
		this.interruptsEnabled = interruptsEnabled;
	}
	
	public void resetDebugPositions() {
		Iterator<Integer> iter = programPositionsOnce.iterator();
		while(iter.hasNext()) {
			int n = iter.next();
			if(n < 0xff) {
				iter.remove();
			}
		}
		
	}

    public void addClockTime(int clockTime) {
        this.clockTime += clockTime;
    }
}

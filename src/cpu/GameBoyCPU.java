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
	private int sp = 0x0000;

	public ArrayList<Integer> programPositionsOnce = new ArrayList<Integer>();
	public ArrayList<Integer> breakPoints = new ArrayList<Integer>();
	public boolean setOnce = false; // todo delete
	private int bit8Operand = 0;
	private int bit16Operand = 0;

	private boolean interruptsEnabled = false;


	private boolean delayedInterruptsEnabled = false;

	private int programCounter = 0;

	public static int stackValue = 0;


	
	
	private static GameBoyCPU singletonInstance;
    private int clockTime;
	private boolean stopExecution;

	private GameBoyCPU() {
		this.mmu = GameBoyMMU.getInstance();
		this.opFact = OpCodeFactory.getInstance();
	}
	public void restart() {
		singletonInstance = null;
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
		//addBreakPoints(  0x02ca);
		long frametimeStart = System.nanoTime();
		while(!stopExecution) {
			cycles = runOperation();
			Clock.addClockTime(cycles);
			if (interruptsEnabled) {
				checkInterrupts();
			}
			
			if (clockTime >= 70224) {
                clockTime = 0;
                busyWait(frametimeStart);
				frametimeStart = System.nanoTime();
			}
//			if(programCounter == 0x33) {
//				System.out.println(Util.byteToHex16(sp));
//			}
//
//			if(programCounter == 0x2836) {
//				System.out.println(Util.byteToHex16(sp));
//			}


//			if (programCounter == 0x0384) {
//					int i = 0;
//			}
		}
	}


	private void busyWait(long frametimeStart) {
		while(frametimeStart + 1.6e7 >= System.nanoTime()) {
			//WAIT
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
			if(breakPoints.contains(programCounter)) {
				System.out.println("before running code at " + Util.byteToHex16(programCounter));
			}
			setProgramCounter(getProgramCounter() + op.getInstructionSize());


			cycles = op.runCode(this, mmu);
			stackWatcher(op);
			if(breakPoints.contains(programCounter - op.getInstructionSize())) {
				System.out.println("after running code at " + Util.byteToHex16(programCounter - op.getInstructionSize()));
			}
			if(delayedInterruptsEnabled) {
				delayedInterruptsEnabled = false;
				interruptsEnabled = true;
			}
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
    	Interrupts interrupts = Util.getInterrupts();

		if(interrupts.isVblankInterruptEnabled() && interrupts.hasVblankInterruptOccurred()) {
			interrupts.resetVblankInterrupt();
			setInterruptsEnabled(false);
			pushSP(programCounter);
			stackWatcher("PUSH");
			setProgramCounter(0x40);
			return;
		}
		
		if(interrupts.isLCDStatInterruptEnabled() && interrupts.hasLCDStatInterruptOccurred()) {
			interrupts.resetLCDStatInterrupt();
			setInterruptsEnabled(false);
			pushSP(programCounter);
			stackWatcher("PUSH");
			setProgramCounter(0x48);
			return;
		}
//
		if(interrupts.isTimerInterruptEnabled() && interrupts.hasTimerInterruptOccurred()) {
			interrupts.resetTimerInterrupt();
			setInterruptsEnabled(false);
			pushSP(programCounter);
			stackWatcher("PUSH");
			setProgramCounter(0x50);
			return;
		}

		if(interrupts.isSerialInterruptEnabled() && interrupts.hasSerialInterruptOccurred()) {
			interrupts.resetSerialInterrupt();
			setInterruptsEnabled(false);
			pushSP(programCounter);
			stackWatcher("PUSH");
			setProgramCounter(0x58);
			return;
		}

		if(interrupts.isJoypadInterruptEnabled() && interrupts.hasJoypadInterruptOccurred()) {
			interrupts.resetJoypadInterrupt();
			setInterruptsEnabled(false);
			pushSP(programCounter);
			stackWatcher("PUSH");
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

	public int getProgramCounter() {
		return programCounter;
	}

	public void setProgramCounter(int newAddress) {
		this.programCounter = newAddress & 0xffff;
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
		sp = data & 0xffff;
		
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
		return sp;
				
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

	public void stackWatcher(OpCode opcode) {
		try {
			if(opcode.toString().contains("LD SP")) {
				stackValue = get16BitOperand();
			}
			if(opcode.toString().contains("RET") || opcode.toString().contains("POP")) {
				stackValue = stackValue + 2;
			}
			if(opcode.toString().contains("CALL") || opcode.toString().contains("PUSH") || opcode.toString().contains("RST")) {
				stackValue = stackValue - 2;
			}
			if(!(stackValue == sp)) {
				throw new Exception(Util.byteToHex16(programCounter) +": stackValue: " + Util.byteToHex16(stackValue) + " sp: " + Util.byteToHex16(sp));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			stopExecution = true;
		}
	}

	public void stackWatcher(String pushOrPop) {
		try {
			if(pushOrPop.equalsIgnoreCase("PUSH")) {
				stackValue = stackValue - 2;
			}
			else {
				stackValue = stackValue + 2;
			}
			if(!(stackValue == sp)) {
				throw new Exception(Util.byteToHex16(programCounter) +": stackValue: " + Util.byteToHex16(stackValue) + " sp: " + Util.byteToHex16(sp));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			stopExecution = true;
		}
	}

	public void pushSP(int data) {
		int leftByte = (byte)(data & 0xff);
		int rightByte = (byte)((data >> 8) & 0xFF);
		setSP((sp - 1) & 0xffff);
		Util.getMemory().setMemoryAtAddress(getSP(), leftByte);
		setSP((sp - 1) & 0xffff);
		Util.getMemory().setMemoryAtAddress(getSP(), rightByte);
	}

	public int popSP() {
		int leftByte = Util.getMemory().getMemoryAtAddress(getSP());
		setSP((sp + 1) & 0xffff);
		int rightByte = Util.getMemory().getMemoryAtAddress(getSP());
		setSP((sp + 1) & 0xffff);
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


	public void setDelayedInterruptsEnabled(boolean delayedInterruptsEnabled) {
		this.delayedInterruptsEnabled = delayedInterruptsEnabled;
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


	private void addBreakPoints(int ...a) {
		for (int i: a) {
			breakPoints.add(i);
		}
	}



}

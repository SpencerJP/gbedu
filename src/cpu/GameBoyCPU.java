package cpu;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpu.opcodetypes.MissingOpCodeException;
import cpu.opcodetypes.OpCode;
import cpu.opcodetypes.OpCodeJump;
import gpu.GameBoyGPU;
import main.Util;
import mmu.GameBoyMMU;

import javax.swing.*;

public class GameBoyCPU {

	public boolean isJumping = false;
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



	private int programCounter = 0;
	
	
	private static GameBoyCPU singletonInstance;
	
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
		boolean runOnce = true;
		while(true) {
			cycles = runOperation();
			gpu.addClockTime(cycles);
			gpu.run();
//			if (programCounter == 0xc) {
//				System.out.println("Zeroed VRAM");
//			}
			if(runOnce && programCounter == 0x55) {
				gpu.dumpVram();
//			    gpu.dumpTileset();
//				gpu.dumpBackgroundTilemap(0);
//			gpu.dumpSetBackgroundTilemap(0);
//				gpu.printBackgroundTilemap(0);
//				int[] tileSpots = {0x19, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18};
//				for(int i = 0; i < tileSpots.length; i++) {
//					gpu.printTile(tileSpots[i]);
//				}
				//gpu.printTile( 0x9910);
				//gpu.printTile( 0, 0x18);
				runOnce = false;
			}
			if (programCounter == 0x4f) {
				//System.out.println(Util.byteToHex16(getHL()));
			}
			if (programCounter == 0x51) {
				//System.out.println(Util.byteToHex16(getHL()));
			}

			if(programCounter == 0x42) {
				//System.out.println(Util.byteToHex(a));
			}
            //Util.log(gpu.mode.name());
			//Util.log("GPU Clock=" + gpu.clock);

			//Thread.sleep(0300);
			//Util.log("HL: " + Util.byteToHex(h) + " " + Util.byteToHex(l) );
			//Util.log(Util.flagsToString());
		}
	}
	

	private int runOperation() throws Exception {
		int cycles = 0;
		try {

			OpCode op = opFact.constructOpCode(getProgramCounter(), mmu.getMemoryAtAddress(getProgramCounter()));

			if (op == null) {
				throw new MissingOpCodeException(mmu, getProgramCounter());
			}
			Util.log("next opCode: " + Util.byteToHex(mmu.getMemoryAtAddress(getProgramCounter())) + "["+op.toString()+"] at position " + getProgramCounter() + " (0x" + Util.byteToHex(getProgramCounter()) + ")");
			cycles = op.runCode(this, mmu);
			if(!isJumping) {
				setProgramCounter(getProgramCounter() + op.getInstructionSize());
			}
			else {
				isJumping = false;
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
		//System.out.println("moving from " + Util.byteToHex(programCounter) + " to " + Util.byteToHex(newAddress));
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


}

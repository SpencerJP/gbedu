package cpu;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpu.opcodetypes.OpCode;
import cpu.opcodetypes.OpCodeJump;
import main.Util;
import mmu.GameBoyMMU;

public class GameBoyCPU {

	public boolean isJumping = false;
	GameBoyMMU mmu;
	OpCodeFactory opFact;


	private int a = (byte) 0x00; // accumulator
	private int b = (byte) 0b0000000;
	private int c = (byte) 0b0000000;
	private int d = (byte) 0b0000000;
	private int e = (byte) 0b0000000;
	private int h = (byte) 0b0000000;
	private int l = (byte) 0b0000000;
	private int f = (byte) 0b0000000; // flag register
	private int s = (byte) 0b0000000;
	private int p = (byte) 0b0000000;



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
	
	
	
	public void run() {
		int cycles = 0;
		while(getProgramCounter() != 12) {
			Util.log("next opCode: " + Util.byteToHex(mmu.getMemoryAtAddress(getProgramCounter())));
			cycles = runOperation();
			Util.log("HL: " + Util.byteToHex(h) + " " + Util.byteToHex(l) );
			Util.log(Util.flagsToString());
		}
	}
	

	private int runOperation() {
		int cycles = 0;
		try {
			OpCode op = opFact.constructOpCode(getProgramCounter(), mmu.getMemoryAtAddress(getProgramCounter()));
			if (op == null) {
				throw new UnsupportedOperationException("Opcode " + Util.byteToHex(mmu.getMemoryAtAddress(getProgramCounter())) + " has not been implemented.");
			}
			cycles = op.runCode(this, mmu);
			if(!isJumping) {
				setProgramCounter(getProgramCounter() + op.getInstructionSize());
			}
			else {
				isJumping = false;
			}
		}
		catch(UnsupportedOperationException e){

			Util.log(e.getMessage());
			throw new UnsupportedOperationException();
		}
		catch(Exception e) {
			Util.log(e.getMessage());
			e.printStackTrace();
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

	public int getS() {
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}

	public int getProgramCounter() {
		return programCounter;
	}

	public void setProgramCounter(int newAddress) {
		Util.log(Integer.toString(newAddress));
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

	public int getHLAddress() {
		return Util.bytesToAddress(h, l);
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
		setC((byte)(data & 0xff));
		setB((byte)((data >> 8) & 0xff));
		
	}
	public void setDE(int data) {
		setE((byte)(data & 0xff));
		setD((byte)((data >> 8) & 0xff));
		
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
	
	
	
	
}

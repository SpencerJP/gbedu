package cpu;

import java.util.Stack;

import main.Utility;
import mmu.GameBoyMMU;

public class GameBoyCPU {
	
	GameBoyMMU mmu;
	
	private int a = (byte) 0x01; // accumulator
	private int b = (byte) 0b0000000;
	private int c = (byte) 0b0000000;
	private int d = (byte) 0b0000000;
	private int e = (byte) 0b0000000;
	private int h = (byte) 0b0000000;
	private int l = (byte) 0b0000000;
	private int f = (byte) 0b0000000; // flag register
	private int s = (byte) 0b0000000;
	private byte p = (byte) 0b0000000;
	public int programCounter = 0;
	
	public GameBoyCPU(GameBoyMMU memory) {
		this.mmu = memory;
	}
	
	public void run() {
		int i = 0;
		System.out.println(a);
		System.out.println(Utility.toBinaryString((byte) a));
		int cycles = 0;
		while(i < 2) {
			System.out.println("next opCode: " +Utility.byteToHex(mmu.getMemoryAtAddress(programCounter)));
			cycles = runOperation();
			i++;
		}
		System.out.println(Utility.byteToHex(s) + " " + Utility.byteToHex(p) );
		System.out.println(Utility.toBinaryString(a));
	}
	

	private int runOperation() {
		int cycles = 0;
		OpCode op = new OpCode(this, mmu.getMemoryAtAddress(programCounter), programCounter);
		cycles = op.runCode(mmu);
		return cycles;
		
	}

	public int getA() {
		return a;
	}

	public void setA(byte a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(byte b) {
		this.b = b;
	}

	public int getC() {
		return c;
	}

	public void setC(byte c) {
		this.c = c;
	}

	public int getD() {
		return d;
	}

	public void setD(byte d) {
		this.d = d;
	}

	public int getE() {
		return e;
	}

	public void setE(byte e) {
		this.e = e;
	}

	public int getF() {
		return f;
	}

	public void setF(byte f) {
		this.f = f;
	}

	public int getS() {
		return s;
	}

	public void setS(byte s) {
		this.s = s;
	}

	public int getP() {
		return p;
	}

	public void setP(byte p) {
		this.p = p;
	}

	public int getOpAddress() {
		return programCounter;
	}

	public void setOpAddress(int opAddress) {
		this.programCounter = opAddress;
	}

	public int getH() {
		return h;
	}

	public void setH(byte h) {
		this.h = h;
	}

	public int getL() {
		return l;
	}

	public void setL(byte l) {
		this.l = l;
	}

	public int getHLAddress() {
		return Utility.bytesToAddress(h, l);
	}
	
	
	
	
}

package cpu;

import java.util.Stack;

import cpu.opcodetypes.OpCode;
import main.Utility;
import mmu.GameBoyMMU;

public class GameBoyCPU {
	
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
	public int programCounter = 0;
	
	
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
		OpCode op = opFact.constructOpCode(mmu.getMemoryAtAddress(programCounter));
		try {
			cycles = op.runCode(this, mmu);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		programCounter = programCounter + (op.getInstructionSize());
		System.out.println("programCounter: " + programCounter);
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

	public int getOpAddress() {
		return programCounter;
	}

	public void setOpAddress(int opAddress) {
		this.programCounter = opAddress;
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
		return Utility.bytesToAddress(h, l);
	}

	public void setHL(int data) {
		setL((data & 0xff));
		setH((data >> 8) & 0xff);
	}

	public void setSP(int data) {
		System.out.println(Utility.byteToHex(data));
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

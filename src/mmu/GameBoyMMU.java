package mmu;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class GameBoyMMU {
	
	private File file;
	private byte[] memory = new byte[65536];
	private int length = -1;
	private static GameBoyMMU singletonInstance;
	
	private GameBoyMMU() {
				
	}
	
	public void initialize(String filename) throws IOException {
		file = new File(filename); 
		  
		FileInputStream fstream = new FileInputStream(file);
		
		try {
			this.length = (new Long(file.length())).intValue();
			memory = new byte[(new Long(file.length())).intValue()];
			fstream.read(memory, 0, (new Long(file.length())).intValue());
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			fstream.close();
		}
	}
	
	public static GameBoyMMU getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new GameBoyMMU();
		}
		return singletonInstance;
	}
	
	public int getMemoryAtAddress(int address) throws ArrayIndexOutOfBoundsException {
		return memory[address] & 0xFF;
		
	}
	
	public byte[] getMemory() {
		return memory;
	}

	public int getLength() {
		return length;
	}

	public void setMemoryAtAddress(int address, int source) {
		memory[address] = (byte) source;
	}

	

	
	
}

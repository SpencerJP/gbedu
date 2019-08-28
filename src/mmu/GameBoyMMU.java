package mmu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class GameBoyMMU {
	
	private File file;
	private byte[] memory = new byte[65536];
	private int length = -1;
	
	public GameBoyMMU(String filename) throws Exception {
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
	
	public byte getMemoryAtAddress(int address) throws ArrayIndexOutOfBoundsException {
		return memory[address];
		
	}
	
	public byte[] getMemory() {
		return memory;
	}

	public int getLength() {
		return length;
	}

	public void setMemoryAtAddress(int address, byte data) {
		memory[address] = data;
	}

	

	
	
}

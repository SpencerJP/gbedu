package mmu;


import bootrom.BootRom;
import cpu.Clock;
import gpu.GameBoyGPU;
import main.Util;
import gpu.GpuRegisters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import cpu.GameBoyCPU;


public class GameBoyMMU {

	private File file;
	private int[] addressSpace;
	public boolean disableBootrom;
	private static GameBoyMMU singletonInstance;
	private IORegisters ior;
	private Interrupts interrupts;
	
	private GameBoyMMU() {
		addressSpace = new int[65536];
		ior = new IORegisters(this);
		interrupts = new Interrupts();

	}

	public void restart() {
		singletonInstance = null;
	}
	
	public void initialize(String filename, boolean disableBootrom) throws IOException {
	    this.disableBootrom = disableBootrom;
	    if(disableBootrom) {
	        Util.getCPU().setProgramCounter(0x100);
	        Util.getGPU().enableLCD();
        }
		file = new File("roms/"+ filename);

		FileInputStream fStream = new FileInputStream(file);
		
		try {
			byte[] buff = new byte[65536];
			int loadromLength = (new Long(file.length())).intValue();
			fStream.read(buff, 0, loadromLength);
            for(int i = 0; i < buff.length; i++) {
            	addressSpace[i] = buff[i];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
            fStream.close();
		}
		powerUp();
	}

	private void powerUp() {
	}

	public static GameBoyMMU getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new GameBoyMMU();
		}
		return singletonInstance;
	}
	
	public int getMemoryAtAddress(int address) throws ArrayIndexOutOfBoundsException {
		if(address < 0x100) {
            if(disableBootrom) {
                return addressSpace[address] & 0xFF;
            }
			return BootRom.data[address] & 0xFF;
		}
        if(address >= 0xff00 && address < 0xff80) {
            return getIORegisters(address);
        }
		return addressSpace[address] & 0xFF;
	}

	private int getIORegisters(int address) {
	    if(address >= 0xff04 && address <= 0xff07){
            return Clock.getTimerRegisters(address);
        }
        if((address >= 0xff40 && address <= 0xff47) || address == 0xff4a || address == 0xff4b){
            return GpuRegisters.getRegisters(address);
        }
	    if(address == 0xff00) {
			return Util.getJoypad().getRegister();
	    }
		if(address == Interrupts.INTERRUPT_ENABLED || address ==  Interrupts.INTERRUPT_FLAGS) {
			interrupts.getRegisters(address);
		}

	    return addressSpace[address] & 0xFF;
	}

	public int[] getAddressSpace() {
		return addressSpace;
	}
	
	public void dump() {
		for(int b : addressSpace) {
			System.out.print(Util.byteToHex(b) + " ");
		}
	}

	public static ArrayList<Integer> addressWatchlist = new ArrayList<Integer>();
	public static void addAddressToWatchlist(int address) {
		addressWatchlist.add(address);
	}
	public void setMemoryAtAddress(int address, int source) {
		if((address & 0xff00) == 0xff00) {
            setSpecialRAM(address, source);
			return;
		}
		if(addressWatchlist.contains(address)) {
//			System.out.println("0x"+Util.byteToHex16(address) + " -> 0x" + Util.byteToHex(source) + " / 0b" + Util.toBinaryString(source)  + " at programCounter " + Util.byteToHex16(Util.getCPU().getProgramCounter()));
		}

		switch(address & 0xF000) {
			case 0x8000:
			case 0x9000:
			    Util.getGPU().setVRAM(address, source);
			    Util.getGPU().updateTile(address);
				addressSpace[address] = source;
				if(Util.isDebugMode && address >= 0x9800 && address <= 0x9fff) {
					GameBoyGPU.getInstance().drawDebugDataInSwingThread();
				}
				break;
			case 0xF000:
                addressSpace[address] = source;
			default:
				addressSpace[address] = source;
				break;

		}
	}

	private void setSpecialRAM(int address, int source) {
        addressSpace[address] = source;
		if((address == IORegisters.BOOTROM_STATUS) && source == 0x01) {
			disableBootrom = true;
			GameBoyCPU.getInstance().resetDebugPositions();
			GameBoyGPU.getInstance().resetVRAM();
		}
		if((address >= 0xff40 && address <= 0xff47) || address == 0xff4a || address == 0xff4b){
		    GpuRegisters.setRegisters(address, source);
		    return;
        }
		if(address >= 0xff04 && address <= 0xff07) {
            Clock.setTimerRegisters(address, source);
            return;
        }
		if(address == 0xFF00) {
			Util.getJoypad().setRegister(source & 0x30);
		}
		if(address == Interrupts.INTERRUPT_ENABLED || address ==  Interrupts.INTERRUPT_FLAGS) {
			interrupts.setRegisters(address, source);
		}
	}


	public Interrupts getInterrupts() {
		return interrupts;
	}
}

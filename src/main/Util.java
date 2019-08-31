package main;

import mmu.GameBoyMMU;
import cpu.GameBoyCPU;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

	public static Logger logger = Logger.getLogger("GameBoy");
	static {
		logger.setLevel(Level.INFO);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void log(String s) {
		logger.log(Level.INFO, s);
	}

	public static void log(Level l, String s) {
		logger.log(l, s);
	}
	
	public static String byteToHex(byte num) {
		char[] hexDigits = new char[2];
		hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
		hexDigits[1] = Character.forDigit((num & 0xF), 16);
		return new String(hexDigits);
	}


	public static String byteToHex(int num) {
		char[] hexDigits = new char[2];
		hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
		hexDigits[1] = Character.forDigit((num & 0xF), 16);
		return new String(hexDigits);
	}
	
	public static byte hexToByte(String hexString) {
		int firstDigit = toDigit(hexString.charAt(0));
		int secondDigit = toDigit(hexString.charAt(1));
		return (byte) ((firstDigit << 4) + secondDigit);
	}

	private static int toDigit(char hexChar) {
		int digit = Character.digit(hexChar, 16);
		if(digit == -1) {
			throw new IllegalArgumentException("Invalid Hexadecimal Character: " + hexChar);
		}
		return digit;
	}
	
	public static String encodeHexString(byte[] byteArray) {
	    StringBuffer hexStringBuffer = new StringBuffer();
	    for (int i = 0; i < byteArray.length; i++) {
	        hexStringBuffer.append(byteToHex(byteArray[i]));
	    }
	    return hexStringBuffer.toString();
	}
	
	public static byte[] decodeHexString(String hexString) {
	    if (hexString.length() % 2 == 1) {
	        throw new IllegalArgumentException(
	          "Invalid hexadecimal String supplied.");
	    }
	     
	    byte[] bytes = new byte[hexString.length() / 2];
	    for (int i = 0; i < hexString.length(); i += 2) {
	        bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
	    }
	    return bytes;
	}
	
	public static int bytesToAddress(byte byte1, byte byte2) {
		return (byte1 | byte2 << 8);
	}
	

	public static int bytesToAddress(int byte1, int byte2) {
		return (byte1 | byte2 << 8);
	}
	
	public static String toBinaryString(byte b) {
		return Integer.toBinaryString((int) b);
	}
	public static String toBinaryString(int b) {
		return Integer.toBinaryString( b);
	}

	public static GameBoyCPU getCPU() {
		return GameBoyCPU.getInstance();
	}
	public static GameBoyMMU getMemory() {
		return GameBoyMMU.getInstance();
	}
	
	public static int setBit(int byteValue, int position) {
        return (byteValue | (1 << position)) & 0xff;
    }

	public static int unsetBit(int byteValue, int position) {
		return (byteValue & ~(1 << position)) & 0xff;
	}
	
	public static boolean getBit(int byteValue, int position) {
        return (byteValue & (1 << position)) != 0;
    }


    public static String flagsToString() {
		int f = getCPU().getF();
		String z = getBit(f, 7) ? "1" : "0";
		String n = getBit(f, 6) ? "1" : "0";
		String h = getBit(f, 5) ? "1" : "0";
		String c = getBit(f, 4) ? "1" : "0";
		return "z="+z+"n="+n+"h="+h+"c="+c;


	}

}



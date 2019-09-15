package joypad;

public class Key {
	
	public static int START_BITPOS = 3;
	public static int SELECT_BITPOS = 2;
	public static int B_BITPOS = 1;
	public static int A_BITPOS = 0;
	public static int DOWN_BITPOS = 3;
	public static int UP_BITPOS = 2;
	public static int LEFT_BITPOS = 1;
	public static int RIGHT_BITPOS = 0;
	
	public int bitPos;
	public boolean isPressed;
	public int modeValue = 0;
	public Key(String s, int modeValue) throws Exception {
		this.modeValue = modeValue;
		switch(s.toLowerCase()) {
		case "start":
			bitPos = START_BITPOS;
			break;
		case "select":
			bitPos = SELECT_BITPOS;
			break;
		case "b":
			bitPos = B_BITPOS;
			break;
		case "a":
			bitPos = A_BITPOS;
			break;
		case "down":
			bitPos = DOWN_BITPOS;
			break;
		case "up":
			bitPos = UP_BITPOS;
			break;
		case "left":
			bitPos = LEFT_BITPOS;
			break;
		case "right":
			bitPos = RIGHT_BITPOS;
			break;
		default:
			throw new Exception("invalid binding");
		}
	}
	 
	

}

package joypad;

public class Key {
	
	public static int START_VALUE = 0x80;
	public static int SELECT_VALUE = 0x40;
	public static int B_VALUE = 0x20;
	public static int A_VALUE = 0x10;
	public static int DOWN_VALUE = 0x8;
	public static int UP_VALUE = 0x4;
	public static int LEFT_VALUE = 0x2;
	public static int RIGHT_VALUE = 0x1;
	
	public int value;
	public boolean isPressed;
	public Key(String s) throws Exception {
		switch(s.toLowerCase()) {
		case "start":
			value = START_VALUE;
			break;
		case "select":
			value = SELECT_VALUE;
			break;
		case "b":
			value = B_VALUE;
			break;
		case "a":
			value = A_VALUE;
			break;
		case "down":
			value = DOWN_VALUE;
			break;
		case "up":
			value = UP_VALUE;
			break;
		case "left":
			value = LEFT_VALUE;
			break;
		case "right":
			value = RIGHT_VALUE;
			break;
		default:
				throw new Exception("invalid binding");
		}
	}
	 
	

}

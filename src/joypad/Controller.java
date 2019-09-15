package joypad;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import main.Util;
import mmu.Interrupts;

public class Controller implements KeyListener {
	public int joypadRegister = 0;
	private int buttonMode = 0;
	private static Controller singletonController;

	public HashMap<Integer, Key> keyBindings = new HashMap<Integer, Key>();
	private boolean bothBitsSet;

	private Controller() {
		try {
			keyBindings.put(KeyEvent.VK_Z, new Key("a", 4));
			keyBindings.put(KeyEvent.VK_X, new Key("b", 4));
			keyBindings.put(KeyEvent.VK_ENTER, new Key("start", 4));
			keyBindings.put(KeyEvent.VK_BACK_SPACE, new Key("select", 4));
			keyBindings.put(KeyEvent.VK_UP, new Key("up", 5));
			keyBindings.put(KeyEvent.VK_DOWN, new Key("down", 5));
			keyBindings.put(KeyEvent.VK_LEFT, new Key("left", 5));
			keyBindings.put(KeyEvent.VK_RIGHT, new Key("right", 5));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Controller getInstance() {
		if(singletonController == null) {
			singletonController = new Controller();
		}
		return singletonController;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		//blank
	}

	@Override
	public void keyPressed(KeyEvent e) {
		try {
			keyBindings.get(e.getKeyCode()).isPressed = true;
			calculateAndSetKPRegister();
			Util.getInterrupts().setJoypadInterrupt();
		}
		catch(NullPointerException ex) {
			
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		try {
			keyBindings.get(e.getKeyCode()).isPressed = false;
			calculateAndSetKPRegister();
			Util.getInterrupts().setJoypadInterrupt();
		}
		catch(NullPointerException ex) {
			
		}
		
	}

	public void calculateAndSetKPRegister() {
		int registerValue = 0;
		for(Key k : keyBindings.values()) {
			if(!k.isPressed && (k.modeValue == buttonMode)) {
				registerValue = registerValue | (1 << k.bitPos);
			}
		}
		if(bothBitsSet) {
			registerValue |= 0x30;
		}
		else {
			registerValue = registerValue | (1 << buttonMode);
		}
		joypadRegister = registerValue;
	}

	public int getRegister() {
		return joypadRegister;
	}

	public void setRegister(int source) {
		bothBitsSet = false;
		switch(source) {
			case 0x10:
				buttonMode = 4;
				break;
			case 0x20:
				buttonMode = 5;
				break;
			case 0x30:
				buttonMode = 5;
				bothBitsSet = true;
		}
		calculateAndSetKPRegister();
	}
}

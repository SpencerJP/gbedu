package joypad;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import main.Util;
import mmu.Interrupts;

public class Controller implements KeyListener {
	public int joypadRegister = 0;
	private static Controller singletonController;

	public HashMap<Integer, Key> keyBindings = new HashMap<Integer, Key>();
	
	private Controller() {
		try {
			keyBindings.put(KeyEvent.VK_Z, new Key("a"));
			keyBindings.put(KeyEvent.VK_X, new Key("b"));
			keyBindings.put(KeyEvent.VK_ENTER, new Key("start"));
			keyBindings.put(KeyEvent.VK_BACK_SPACE, new Key("select"));
			keyBindings.put(KeyEvent.VK_UP, new Key("up"));
			keyBindings.put(KeyEvent.VK_DOWN, new Key("down"));
			keyBindings.put(KeyEvent.VK_LEFT, new Key("left"));
			keyBindings.put(KeyEvent.VK_RIGHT, new Key("right"));
			
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
		}
		catch(NullPointerException ex) {
			
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		try {
			keyBindings.get(e.getKeyCode()).isPressed = false;
			calculateAndSetKPRegister();
			Interrupts.setJoypadInterrupt();			
		}
		catch(NullPointerException ex) {
			
		}
		
	}

	public void calculateAndSetKPRegister() {
		int registerValue = 0;
		for(Key k : keyBindings.values()) {
			if(k.isPressed) {
				registerValue += k.value;
			}
		}
		joypadRegister = registerValue;
	}
}

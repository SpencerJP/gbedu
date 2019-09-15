package cpu;

import main.Util;
import mmu.Interrupts;

public class Clock  {


    public static final int[] settings = {4096, 262144, 65536, 16384};
    public static int timerClock = 0;
    public static int timerCounter = 0;
    public static int timerModulo = 0;
    public static int dividerClock = 0;
    public static int dividerCounter = 0;
    public static int timerMode = 0;
    public static boolean timerIsRunning = false;


    public static int getTimerRegisters(int address) {
        switch(address) {
            case 0xff04:
                return dividerCounter;
            case 0xff05:
                return timerCounter;
            case 0xff06:
                return timerModulo;
            case 0xff07:
                int i = 0;
                i |= timerMode;
                if(timerIsRunning) {
                    i |= 0b100;
                }
                return i;
            default:
                throw new UnsupportedOperationException("Clock: getTimerAddress()");
        }
    }

    public static void setTimerRegisters(int address, int value) {
        switch(address) {
            case 0xff04:
                dividerCounter = 0;
                break;
            case 0xff05:
                // I assume you can't just set this value.
                break;
            case 0xff06:
                timerModulo = value;
                break;
            case 0xff07:
                timerMode = value & 0b11;
                timerIsRunning = (value & 0b100) == 0b100;
                break;
            default:
                throw new UnsupportedOperationException("Clock: getTimerAddress()");
        }
    }

    public static void addClockTime(int clockTime) {
        Util.getCPU().addClockTime(clockTime);
        Util.getGPU().addClockTime(clockTime);
        runTimers(clockTime);
        Util.getGPU().run();
    }

    private static void runTimers(int clockTime) {
        dividerClock += clockTime;
        if(dividerClock >= 16384) {
            dividerClock = 0;
            dividerCounter++;
            if (dividerCounter > 0xff) {
                dividerCounter = 0;
            }
        }

        if(timerIsRunning) {
            timerClock += clockTime;
            if (timerClock >= settings[timerMode]) {
                timerClock = 0;
                timerCounter++;
                if (timerCounter > 0xff) {
                    timerCounter = timerModulo;
                    Util.getInterrupts().setTimerInterrupt();
                }
            }
        }
    }
}


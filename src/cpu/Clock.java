package cpu;

import main.Util;

public class Clock  {

    public static void addClockTime(int i) {
        Util.getGPU().addClockTime(i);
    }
}

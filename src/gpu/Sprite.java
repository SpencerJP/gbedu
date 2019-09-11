package gpu;

import java.awt.Point;

public class Sprite {
	public Point pos = new Point(0,0); // x and y offset from tile
	public int tileNum = 0;  // which tile to be offset from
	public boolean hasPriority = true; // true = above background, false = below background except for color 0
	public boolean yFlip = false; // true = flip y axis, false = default
	public boolean xFlip = false; // true = flip x axis, false = default
	public int palette = 0; // 0 = obj palette 0, 1 = obj palette 1;
}

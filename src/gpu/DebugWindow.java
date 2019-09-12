package gpu;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DebugWindow extends JPanel {

    private BufferedImage canvas;

    public DebugWindow(int width, int height) {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    }

    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }

    public void drawData(Color[] pixels, int scrollX, int scrollY) {
        int x = 0;
        int y = 0;
        if (pixels[0] == null) {
            return;
        }
        for(Color c : pixels) {
            if(x == GameBoyGPU.DEBUG_BACKGROUND_DIMENSION) {
                x = 0;
                y++;
            }
            canvas.setRGB(x, y, c.getRGB());
            x++;
        }
        
    	for(int i = scrollX; i < scrollX + GameBoyGPU.WIDTH_PIXELS; i++) {
    		
    		canvas.setRGB(i, scrollY, 0xFFFFFF);
    		canvas.setRGB(i, scrollY + GameBoyGPU.HEIGHT_PIXELS, 0xFFFFFF);
    		

    		try {
        		canvas.setRGB(i, scrollY - 1, 0xFFFFFF);
    		}catch(Exception e) {}
    		try {
    			canvas.setRGB(i, scrollY + GameBoyGPU.HEIGHT_PIXELS + 1, 0xFFFFFF);
    		}catch(Exception e) {}
    	}
    	for(int i = scrollY; i < scrollY + GameBoyGPU.HEIGHT_PIXELS; i++) {
    		canvas.setRGB(scrollX, i, 0xFFFFFF);
    		canvas.setRGB(scrollX+GameBoyGPU.WIDTH_PIXELS, i, 0xFFFFFF);
    		try {
    			canvas.setRGB(scrollX - 1, i, 0xFFFFFF);
    		}catch(Exception e) {}
    		try {
        		canvas.setRGB(scrollX + GameBoyGPU.WIDTH_PIXELS + 1, i, 0xFFFFFF);
    		}catch(Exception e) {}
    	}
        

    }

    public void drawDataInSwingThread(Color[] pixels, int scrollX, int scrollY) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                drawData(pixels, scrollX, scrollY);
                repaint();
            }
        });
    }
}



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

    public void drawData(Color[] pixels) {
        int x = 0;
        int y = 0;
        if (pixels[0] == null) {
            return;
        }
        int i = 0;
        for(Color c : pixels) {
            if(x == GameBoyGPU.DEBUG_BACKGROUND_DIMENSION) {
                x = 0;
                y++;
            }
            try {
                canvas.setRGB(x, y, c.getRGB());
            }
            catch(Exception e) {
                //System.out.println("pixel " + i);
            }
            x++;
            i++;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }
}

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Watermark {
    private String text;
    private Font font;
    private int x;
    private int y;

    public Watermark(String text, Font font, int x, int y) {
        this.text = text;
        this.font = font;
        this.x = x;
        this.y = y;
    }

    public void applyWatermark(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(font);
        g2d.setColor(java.awt.Color.RED); // Change color as needed
        g2d.drawString(text, x, y);
        g2d.dispose();
    }
}
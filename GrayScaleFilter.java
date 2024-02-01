import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class GrayScaleFilter implements Filter {
    @Override
    public BufferedImage apply(BufferedImage image) {
        BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscaleImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return grayscaleImage;
    }
}

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class SharpenFilter implements Filter{
    @Override
    public BufferedImage apply(BufferedImage image) {
        float[] sharpenMatrix = { 0, -1, 0, -1, 5, -1, 0, -1, 0 };
        Kernel kernel = new Kernel(3, 3, sharpenMatrix);
        ConvolveOp convolveOp = new ConvolveOp(kernel);
        return convolveOp.filter(image, null);
}
}
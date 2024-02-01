import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FilterManager {
    private List<Filter> filters;

    public FilterManager() {
        filters = new ArrayList<>();
        // Initialize filters
        filters.add(new SharpenFilter());
        filters.add(new GrayScaleFilter());
    }

    public BufferedImage applyFilter(BufferedImage image, Filter filter) {
        return filter.apply(image);
    }
}

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PhotoViewer extends JFrame {
    private JLabel imageLabel;
    private BufferedImage currentImage;
    private BufferedImage originalImage;
    private double zoomFactor = 1.0;
    private boolean isCropping = false;
    private Rectangle cropRect = null;
    private FilterManager filterManager;
    
    private JMenuItem sharpenMenuItem;
    private JMenuItem grayscaleMenuItem;

    public PhotoViewer() {
        setTitle("Simple Photo Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeUI();
    }

    private void initializeUI() {
        imageLabel = new JLabel();
        filterManager = new FilterManager();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);

        setupMenus();
        setupButtons();
        setupImageLabelListeners();
    }

    private void setupMenus() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenu filterMenu = new JMenu("Filter");
        menuBar.add(filterMenu);

        sharpenMenuItem = new JMenuItem("Sharpen");
        grayscaleMenuItem = new JMenuItem("Grayscale");

        filterMenu.add(sharpenMenuItem);
        filterMenu.add(grayscaleMenuItem);

        JMenuItem openMenuItem = new JMenuItem("Open");
        fileMenu.add(openMenuItem);

        JMenuItem addWatermarkMenuItem = new JMenuItem("Add Watermark");
        fileMenu.add(addWatermarkMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save");
        fileMenu.add(saveMenuItem);

        openMenuItem.addActionListener(e -> openImage());

        saveMenuItem.addActionListener(e -> saveImage());

        addWatermarkMenuItem.addActionListener(e -> addWatermark());

        sharpenMenuItem.addActionListener(e -> applyFilter(new SharpenFilter()));

        grayscaleMenuItem.addActionListener(e -> applyFilter(new GrayScaleFilter()));
    }

    private void setupButtons() {
        JButton zoomInButton = new JButton("Zoom in");
        JButton zoomOutButton = new JButton("Zoom out");
        JButton cropButton = new JButton("Crop");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(zoomInButton);
        buttonPanel.add(zoomOutButton);
        buttonPanel.add(cropButton);

        add(buttonPanel, BorderLayout.SOUTH);

        zoomInButton.addActionListener(e -> zoomIn());
        zoomOutButton.addActionListener(e -> zoomOut());
        cropButton.addActionListener(e -> startCrop());
    }
    

    private void setupImageLabelListeners() {
        imageLabel.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            if (notches < 0) {
                zoomIn();
            } else {
                zoomOut();
            }
        });

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isCropping) {
                    if (cropRect == null) {
                        cropRect = new Rectangle();
                        cropRect.setLocation(e.getPoint());
                    } else {
                        cropRect.setSize(
                            (int) (e.getX() - cropRect.getX()), 
                            (int) (e.getY() - cropRect.getY())
                        );
                        cropRect.setSize(
                            Math.abs(cropRect.width), 
                            Math.abs(cropRect.height)
                        );
                        isCropping = false;
                        cropImage();
                    }
                }
            }
        });
    }

    private void startCrop() {
        isCropping = true;
        JOptionPane.showMessageDialog(this, "Click and drag to define the crop area.");
    }

    private void cropImage() {
        if (cropRect != null && currentImage != null) {
            BufferedImage croppedImage = currentImage.getSubimage(
                (int) cropRect.getX(), 
                (int) cropRect.getY(), 
                (int) cropRect.getWidth(), 
                (int) cropRect.getHeight()
            );
            currentImage = croppedImage;
            updateImage(currentImage);
        }
    }

    private void updateImage(BufferedImage image) {
        if (image != null) {
            int width = (int) (image.getWidth() * zoomFactor);
            int height = (int) (image.getHeight() * zoomFactor);
            currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = currentImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            g.dispose();
            imageLabel.setIcon(new ImageIcon(currentImage));
        }
    }

    private void applyFilter(Filter filter) {
        if (currentImage != null && filterManager != null) {
            try {
                BufferedImage filteredImage = filterManager.applyFilter(currentImage, filter);
                if (filteredImage != null) {
                    updateImage(filteredImage);
                } else {
                    System.err.println("Filtered image is null. Check filterManager.applyFilter() implementation.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error applying filter: " + e.getMessage());
            }
        } else {
            System.err.println("Current image or filter manager is null.");
        }
    }

    // Define constants for zoom limits
private static final double MIN_ZOOM_FACTOR = 0.6; // Adjust as needed
private static final double MAX_ZOOM_FACTOR = 2.0; // Adjust as needed

private void zoomIn() {
    if (zoomFactor < MAX_ZOOM_FACTOR) {
        zoomFactor *= 1.2; // Adjust this factor as needed
        updateImage(currentImage);
    }
}

private void zoomOut() {
    if (zoomFactor > MIN_ZOOM_FACTOR) {
        zoomFactor /= 1.2; // Adjust this factor as needed
        updateImage(currentImage);
    }
}

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String imagePath = fileChooser.getSelectedFile().getPath();
            loadImage(imagePath);
        }
    }

    private void saveImage() {
        if (currentImage != null) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String savePath = fileChooser.getSelectedFile().getPath();

                try {
                    ImageIO.write(currentImage, "png", new File(savePath));
                    JOptionPane.showMessageDialog(this, "Image saved successfully!");
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error saving image!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void loadImage(String imagePath) {
        try {
            originalImage = ImageIO.read(new File(imagePath));
            currentImage = originalImage;
            updateImage(currentImage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading image!");
        }
    }

    private void addWatermark() {
        String watermarkText = JOptionPane.showInputDialog(this, "Enter watermark text:");
        Font watermarkFont = new Font("Arial", Font.BOLD, 36); // Adjust font as needed
        Watermark watermark = new Watermark(watermarkText, watermarkFont, 20, 40); // Adjust position as needed
        watermark.applyWatermark(currentImage);
        updateImage(currentImage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhotoViewer().setVisible(true));
    }
}

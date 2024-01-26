import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.awt.event.MouseWheelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.RescaleOp;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PhotoViewer extends JFrame {
    private JLabel imageLabel;
    private BufferedImage currentImage;
    private BufferedImage originalImage;
    private double zoomFactor = 1.0;
    private float brightnessFactor = 1.0f;
    private boolean isCropping = false;
    private Rectangle cropRect = null;

    private void initializeUI() {
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenu filterMenu = new JMenu("Filter");
        menuBar.add(filterMenu);

        JMenuItem sharpenMenuItem = new JMenuItem("Sharpen");
        filterMenu.add(sharpenMenuItem);

        JMenuItem brightenMenuItem = new JMenuItem("Brightness");
        filterMenu.add(brightenMenuItem);

        JMenuItem openMenuItem = new JMenuItem("Open");
        fileMenu.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save");
        fileMenu.add(saveMenuItem);

        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        });

        sharpenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sharpenImage();
            }
        });

        brightenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustBrightness();
            }
        });

        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });

        

        JButton zoomInButton = new JButton("Zoom in");
    JButton zoomOutButton = new JButton("Zoom out");

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(zoomInButton);
    buttonPanel.add(zoomOutButton);

    add(buttonPanel, BorderLayout.SOUTH);

    zoomInButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            zoomIn();
        }
    });

    zoomOutButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            zoomOut();
        }
    });

    imageLabel.addMouseWheelListener(new MouseWheelListener() {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int notches = e.getWheelRotation();
            if (notches < 0){
                zoomIn();
            }
            else {
                zoomOut();
            }
        }
    });

    JButton cropButton = new JButton("Crop");
    buttonPanel.add(cropButton);

    cropButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            startCrop();
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
                    cropRect.setSize((int) (e.getX() - cropRect.getX()), (int) (e.getY() - cropRect.getY()));
                    cropRect.setSize(Math.abs(cropRect.width), Math.abs(cropRect.height));
                    isCropping = false;
                    cropImage();
                }
            }
        }
    });

    }

    private void startCrop() {
        isCropping = true;
    }

    private void cropImage() {
        if (cropRect != null && currentImage != null) {
            BufferedImage croppedImage = currentImage.getSubimage((int) cropRect.getX(), (int) cropRect.getY(), (int) cropRect.getWidth(), (int) cropRect.getHeight());
            currentImage = croppedImage;
            updateImage();
        }
    }

    private void updateImage() {
        if (originalImage != null) {
            int width = (int) (originalImage.getWidth() * zoomFactor);
            int height = (int) (originalImage.getHeight() * zoomFactor);
            currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = currentImage.createGraphics();
            g.drawImage(originalImage, 0, 0, width, height, null);
            g.dispose();
            imageLabel.setIcon(new ImageIcon(currentImage));
        }
    }

    private void zoomIn() {
        zoomFactor *= 1.2; // You can adjust the zoom factor based on your preference
        updateImage();
    }

    private void zoomOut() {
        zoomFactor /= 1.2; // You can adjust the zoom factor based on your preference
        updateImage();
    }

    private void openImage(){
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

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String savePath = fileChooser.getSelectedFile().getPath();

            // Create a BufferedImage from the Image
                BufferedImage bufferedImage = new BufferedImage(currentImage.getWidth(null), currentImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
                Graphics g = bufferedImage.getGraphics();
                g.drawImage(currentImage, 0, 0, null);
                g.dispose();

                // Save the BufferedImage to the specified path
                try {
                    ImageIO.write(currentImage, "png", new File(savePath)); // You can change the format as needed (e.g., "jpg", "png", "gif")
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
            imageLabel.setIcon(new ImageIcon(currentImage));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading image!");
        }
    }

    //Use images that aren't already too bright or dark, otherwise the filters won't be very noticable

    // Method to apply sharpen filter
    private void sharpenImage() {
        if (currentImage != null) {
            BufferedImage sharpenedImage = applySharpenFilter(currentImage);
            currentImage = sharpenedImage;
            updateImage();
        }
    }

    // Apply sharpen filter to the image
    private BufferedImage applySharpenFilter(BufferedImage image) {
        float[] sharpenMatrix = { 0, -1, 0, -1, 5, -1, 0, -1, 0 };
        Kernel kernel = new Kernel(3, 3, sharpenMatrix);
        ConvolveOp convolveOp = new ConvolveOp(kernel);
        return convolveOp.filter(image, null);
    }

    private void adjustBrightness() {
        String brightnessValue = JOptionPane.showInputDialog("Enter brightness value (-1.0 to 1.0):");
        try {
            brightnessFactor = Float.parseFloat(brightnessValue);
            if (brightnessFactor < -1.0f || brightnessFactor > 1.0f) {
                throw new NumberFormatException();
            }
            applyBrightnessFilter();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid brightness value!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyBrightnessFilter() {
        if (currentImage != null) {
            BufferedImage brightenedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = brightenedImage.createGraphics();
            float[] factors = {brightnessFactor, brightnessFactor, brightnessFactor};
            float[] offsets = {0.0f, 0.0f, 0.0f}; // No offset
            RescaleOp op = new RescaleOp(factors, offsets, null);
            op.filter(originalImage, brightenedImage);
            g.drawImage(brightenedImage, 0, 0, null);
            g.dispose();
            currentImage = brightenedImage;
            updateImage();
        }
    }

    public PhotoViewer() {
        setTitle("Simple Photo Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeUI();
}
public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run(){
            new PhotoViewer().setVisible(true);
        }
    });
}
}

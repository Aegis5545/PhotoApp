import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PhotoViewer extends JFrame {
    private JLabel imageLabel;
    private ImageIcon currentImage;
    private ImageIcon originalImage;
    private double zoomFactor = 1.0;

    private void initializeUI() {
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem openMenuItem = new JMenuItem("Open");
        fileMenu.add(openMenuItem);

        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
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
    }

    private void updateImage() {
        if (originalImage != null) {
            int width = (int) (originalImage.getIconWidth() * zoomFactor);
            int height = (int) (originalImage.getIconHeight() * zoomFactor);
            Image scaledImage = originalImage.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            currentImage = new ImageIcon(scaledImage);
            imageLabel.setIcon(currentImage);
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

    private void loadImage(String imagePath) {
        currentImage = new ImageIcon(imagePath);
        originalImage = currentImage;
        imageLabel.setIcon(currentImage);
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ShoeStoreUI extends JFrame {
    private Image detailImg;
    private String detailNameStr = "";
    private String detailPriceStr = "";
    private String detailBrandStr = "";
    private String detailDescStr = "";
    private FadePanel detailPanel;
    private float detailAlpha = 1.0f;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShoeStoreUI(new String[] {
                "img1.png", "img2.png", "img3.png", "img4.png", "img5.png", "img6.png"
        }));
    }

    public ShoeStoreUI(String[] imagePaths) {
        setTitle("Adidas Shoe Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        // LEFT: Chi tiết sản phẩm
        detailPanel = new FadePanel();
        detailPanel.setPreferredSize(new Dimension(300, 600));
        detailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // RIGHT: Danh sách sản phẩm
        JPanel listPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < imagePaths.length; i++) {
            String name = "4DFWD PULSE SHOES " + (i + 1);
            String price = "$" + (100 + i * 10) + ".00";
            String brand = "Adidas";
            String desc = "This product is excluded from all promotional discounts and offers.";
            String img = imagePaths[i];

            JPanel card = createShoeCard(name, price, brand, desc, img);
            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    updateDetailPanel(name, price, brand, desc, img);
                }
            });
            listPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        add(detailPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);

        // Hiển thị sản phẩm đầu tiên
        updateDetailPanel("4DFWD PULSE SHOES 1", "$100.00", "Adidas",
                "This product is excluded from all promotional discounts and offers.", imagePaths[0]);
    }

    private void updateDetailPanel(String name, String price, String brand, String desc, String imgPath) {
        // Hiệu ứng fade out
        Timer timer = new Timer(20, null);
        final float[] alpha = { 1.0f };
        timer.addActionListener(e -> {
            alpha[0] -= 0.1f;
            detailAlpha = alpha[0];
            if (alpha[0] <= 0f) {
                timer.stop();
                // Cập nhật thông tin mới
                ImageIcon icon = new ImageIcon(imgPath);
                detailImg = icon.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
                detailNameStr = name;
                detailPriceStr = price;
                detailBrandStr = brand;
                detailDescStr = desc;
                // Hiệu ứng fade in
                Timer fadeIn = new Timer(20, null);
                alpha[0] = 0f;
                fadeIn.addActionListener(ev -> {
                    alpha[0] += 0.1f;
                    detailAlpha = alpha[0];
                    if (alpha[0] >= 1f) {
                        alpha[0] = 1f;
                        fadeIn.stop();
                    }
                    detailPanel.repaint();
                });
                fadeIn.start();
            }
            detailPanel.repaint();
        });
        timer.start();
    }

    private JPanel createShoeCard(String name, String price, String brand, String desc, String imagePath) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 200));

        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(img));
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(imgLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(priceLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(descLabel);

        return card;
    }

    // Panel chi tiết sản phẩm với hiệu ứng mờ dần
    class FadePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, detailAlpha));

            if (detailImg != null) {
                g2d.drawImage(detailImg, 20, 20, 200, 130, null);
            }
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.setColor(Color.BLACK);
            g2d.drawString(detailNameStr, 20, 180);

            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString(detailPriceStr, 20, 210);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString(detailBrandStr, 20, 232);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            drawStringMultiLine(g2d, detailDescStr, 20, 254);

            g2d.dispose();
        }

        private void drawStringMultiLine(Graphics2D g2d, String text, int x, int y) {
            FontMetrics fm = g2d.getFontMetrics();
            int maxWidth = detailPanel.getWidth() - 40;

            for (String paragraph : text.split("\n")) {
                String line = "";
                for (String word : paragraph.split(" ")) {
                    String testLine = line.isEmpty() ? word : line + " " + word;
                    int width = fm.stringWidth(testLine);
                    if (width > maxWidth) {
                        g2d.drawString(line, x, y);
                        line = word;
                        y += fm.getHeight();
                    } else {
                        line = testLine;
                    }
                }
                if (!line.isEmpty()) {
                    g2d.drawString(line, x, y);
                    y += fm.getHeight();
                }
            }
        }
    }
}
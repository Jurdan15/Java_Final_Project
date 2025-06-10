package jerald;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Inventory System Dashboard");

        // Layout and appearance
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(230, 230, 250)); // Light lavender

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font btnFont = new Font("Arial", Font.BOLD, 16);

        JButton btnSupplier = new JButton("Manage Suppliers");
        JButton btnProduct = new JButton("Manage Products");
        JButton btnPurchaseOrder = new JButton("Create Purchase Order");

        btnSupplier.setFont(btnFont);
        btnProduct.setFont(btnFont);
        btnPurchaseOrder.setFont(btnFont);

        btnSupplier.setBackground(new Color(100, 149, 237));       // Cornflower blue
        btnProduct.setBackground(new Color(60, 179, 113));         // Medium sea green
        btnPurchaseOrder.setBackground(new Color(255, 165, 0));    // Orange

        btnSupplier.setForeground(Color.WHITE);
        btnProduct.setForeground(Color.WHITE);
        btnPurchaseOrder.setForeground(Color.WHITE);

        // Action Listeners
        btnSupplier.addActionListener(e -> {
            new SupplierForm(); // Make sure SupplierForm exists
        });

        btnProduct.addActionListener(e -> {
            new ProductForm();
        });

        btnPurchaseOrder.addActionListener(e -> {
            new PurchaseOrderForm();
        });

        // Add buttons to panel
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(btnSupplier, gbc);
        gbc.gridy++;
        panel.add(btnProduct, gbc);
        gbc.gridy++;
        panel.add(btnPurchaseOrder, gbc);

        add(panel);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new);
    }
}

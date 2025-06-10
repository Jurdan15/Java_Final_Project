package jerald;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class PurchaseOrderForm extends JFrame {
    private JComboBox<String> cbSuppliers, cbProducts;
    private JTextField txtQuantity;
    private JButton btnOrder, btnBack;

    public PurchaseOrderForm() {
        setTitle("Create Purchase Order");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblSupplier = new JLabel("Supplier:");
        JLabel lblProduct = new JLabel("Product:");
        JLabel lblQuantity = new JLabel("Quantity:");

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        lblSupplier.setFont(labelFont);
        lblProduct.setFont(labelFont);
        lblQuantity.setFont(labelFont);

        cbSuppliers = new JComboBox<>(loadSuppliers());
        cbProducts = new JComboBox<>(loadProducts());
        txtQuantity = new JTextField(10);

        btnOrder = new JButton("Place Order");
        btnOrder.setBackground(new Color(30, 144, 255));
        btnOrder.setForeground(Color.WHITE);
        btnOrder.setFont(new Font("Arial", Font.BOLD, 14));

        btnBack = new JButton("Back");
        btnBack.setBackground(new Color(220, 53, 69)); // Bootstrap danger red
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblSupplier, gbc);
        gbc.gridx = 1;
        panel.add(cbSuppliers, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblProduct, gbc);
        gbc.gridx = 1;
        panel.add(cbProducts, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblQuantity, gbc);
        gbc.gridx = 1;
        panel.add(txtQuantity, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnOrder, gbc);

        gbc.gridy = 4;
        panel.add(btnBack, gbc);

        // Actions
        btnOrder.addActionListener(e -> placeOrder());
        btnBack.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        add(panel);
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Vector<String> loadSuppliers() {
        Vector<String> suppliers = new Vector<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventorydb", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SupplierID, Name FROM Suppliers")) {
            while (rs.next()) {
                suppliers.add(rs.getInt("SupplierID") + " - " + rs.getString("Name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return suppliers;
    }

    private Vector<String> loadProducts() {
        Vector<String> products = new Vector<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventorydb", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ProductID, Name FROM Products")) {
            while (rs.next()) {
                products.add(rs.getInt("ProductID") + " - " + rs.getString("Name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return products;
    }

    private void placeOrder() {
        try {
            int supplierId = Integer.parseInt(cbSuppliers.getSelectedItem().toString().split(" - ")[0]);
            int productId = Integer.parseInt(cbProducts.getSelectedItem().toString().split(" - ")[0]);
            int quantity = Integer.parseInt(txtQuantity.getText());

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventorydb", "root", "");
            conn.setAutoCommit(false);

            String insertOrder = "INSERT INTO PurchaseOrders (SupplierID, ProductID, Quantity) VALUES (?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(insertOrder);
            ps1.setInt(1, supplierId);
            ps1.setInt(2, productId);
            ps1.setInt(3, quantity);
            ps1.executeUpdate();

            String updateStock = "UPDATE Products SET QuantityInStock = QuantityInStock + ? WHERE ProductID = ?";
            PreparedStatement ps2 = conn.prepareStatement(updateStock);
            ps2.setInt(1, quantity);
            ps2.setInt(2, productId);
            ps2.executeUpdate();

            conn.commit();
            conn.close();

            JOptionPane.showMessageDialog(this, "Purchase order successful!");
            txtQuantity.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Transaction failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PurchaseOrderForm::new);
    }
}

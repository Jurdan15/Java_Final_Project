package jerald;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ProductForm extends JFrame {
    private JTextField txtName, txtPrice, txtStock;
    private JButton btnAdd, btnDelete, btnBack;
    private JComboBox<String> cbProducts;

    public ProductForm() {
        setTitle("Manage Products");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        JLabel lblName = new JLabel("Product Name:");
        JLabel lblPrice = new JLabel("Price:");
        JLabel lblStock = new JLabel("Initial Stock:");
        JLabel lblDelete = new JLabel("Select Product to Delete:");
        lblName.setFont(labelFont);
        lblPrice.setFont(labelFont);
        lblStock.setFont(labelFont);
        lblDelete.setFont(labelFont);

        txtName = new JTextField(15);
        txtPrice = new JTextField(15);
        txtStock = new JTextField(15);
        cbProducts = new JComboBox<>(loadProductList());

        btnAdd = new JButton("Add Product");
        btnDelete = new JButton("Delete Product");
        btnBack = new JButton("Back to Dashboard");

        // Styling
        btnAdd.setBackground(new Color(34, 139, 34));
        btnAdd.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(178, 34, 34));
        btnDelete.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(70, 130, 180)); // Steel Blue
        btnBack.setForeground(Color.WHITE);

        btnAdd.setFont(labelFont);
        btnDelete.setFont(labelFont);
        btnBack.setFont(labelFont);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblName, gbc);
        gbc.gridx = 1;
        panel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblPrice, gbc);
        gbc.gridx = 1;
        panel.add(txtPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblStock, gbc);
        gbc.gridx = 1;
        panel.add(txtStock, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnAdd, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblDelete, gbc);
        gbc.gridx = 1;
        panel.add(cbProducts, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnDelete, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        panel.add(btnBack, gbc);

        // Actions
        btnAdd.addActionListener(e -> addProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnBack.addActionListener(e -> {
            dispose(); // Close this window
            new Dashboard(); // Open dashboard
        });

        add(panel);
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addProduct() {
        String name = txtName.getText();
        String priceStr = txtPrice.getText();
        String stockStr = txtStock.getText();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/inventorydb", "root", ""
            );

            String sql = "INSERT INTO Products (Name, Price, QuantityInStock) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, stock);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Product added successfully!");
            txtName.setText("");
            txtPrice.setText("");
            txtStock.setText("");
            conn.close();

            cbProducts.setModel(new DefaultComboBoxModel<>(loadProductList()));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteProduct() {
        String selected = (String) cbProducts.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No product selected.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int productId = Integer.parseInt(selected.split(" - ")[0]);
                Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/inventorydb", "root", ""
                );
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Products WHERE ProductID = ?");
                ps.setInt(1, productId);
                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                cbProducts.setModel(new DefaultComboBoxModel<>(loadProductList()));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private String[] loadProductList() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventorydb", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ProductID, Name FROM Products");

            Vector<String> list = new Vector<>();
            while (rs.next()) {
                list.add(rs.getInt("ProductID") + " - " + rs.getString("Name"));
            }
            conn.close();
            return list.toArray(new String[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new String[]{"No products found"};
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductForm::new);
    }
}

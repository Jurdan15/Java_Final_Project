package jerald;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class SupplierForm extends JFrame {
    private JTextField txtName, txtContact;
    private JButton btnAdd, btnDelete, btnUpdate, btnBack;
    private JComboBox<String> cbSuppliers;

    public SupplierForm() {
        setTitle("Manage Suppliers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Supplier Management");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 5, 20, 5);
        panel.add(lblTitle, gbc);

        // Name
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("Supplier Name:"), gbc);

        gbc.gridx = 1;
        txtName = new JTextField(20);
        panel.add(txtName, gbc);

        // Contact
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Contact Info:"), gbc);

        gbc.gridx = 1;
        txtContact = new JTextField(20);
        panel.add(txtContact, gbc);

        // Supplier ComboBox (for delete/update)
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Select Supplier:"), gbc);

        gbc.gridx = 1;
        cbSuppliers = new JComboBox<>(loadSupplierList());
        cbSuppliers.addActionListener(e -> populateSelectedSupplier());
        panel.add(cbSuppliers, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnBack = new JButton("Back");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);

        panel.add(buttonPanel, gbc);

        // Action Listeners
        btnAdd.addActionListener(e -> addSupplier());
        btnDelete.addActionListener(e -> deleteSupplier());
        btnUpdate.addActionListener(e -> updateSupplier());
        btnBack.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addSupplier() {
        String name = txtName.getText();
        String contact = txtContact.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/inventorydb", "root", ""
            );
            String sql = "INSERT INTO Suppliers (Name, ContactInfo) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, contact);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Supplier added!");
            txtName.setText("");
            txtContact.setText("");
            conn.close();

            cbSuppliers.setModel(new DefaultComboBoxModel<>(loadSupplierList()));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteSupplier() {
        String selected = (String) cbSuppliers.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No supplier selected.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this supplier?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int supplierId = Integer.parseInt(selected.split(" - ")[0]);
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/inventorydb", "root", ""
                );
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Suppliers WHERE SupplierID = ?");
                ps.setInt(1, supplierId);
                ps.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(this, "Supplier deleted!");
                cbSuppliers.setModel(new DefaultComboBoxModel<>(loadSupplierList()));
                txtName.setText("");
                txtContact.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void updateSupplier() {
        String selected = (String) cbSuppliers.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No supplier selected.");
            return;
        }

        try {
            int supplierId = Integer.parseInt(selected.split(" - ")[0]);
            String name = txtName.getText();
            String contact = txtContact.getText();

            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/inventorydb", "root", ""
            );
            String sql = "UPDATE Suppliers SET Name = ?, ContactInfo = ? WHERE SupplierID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setInt(3, supplierId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Supplier updated!");
            cbSuppliers.setModel(new DefaultComboBoxModel<>(loadSupplierList()));
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void populateSelectedSupplier() {
        String selected = (String) cbSuppliers.getSelectedItem();
        if (selected == null) return;

        try {
            int supplierId = Integer.parseInt(selected.split(" - ")[0]);
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/inventorydb", "root", ""
            );
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Suppliers WHERE SupplierID = ?");
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtName.setText(rs.getString("Name"));
                txtContact.setText(rs.getString("ContactInfo"));
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String[] loadSupplierList() {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/inventorydb", "root", ""
            );
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SupplierID, Name FROM Suppliers");

            Vector<String> list = new Vector<>();
            while (rs.next()) {
                list.add(rs.getInt("SupplierID") + " - " + rs.getString("Name"));
            }
            conn.close();
            return list.toArray(new String[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new String[]{"No suppliers found"};
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SupplierForm::new);
    }
}

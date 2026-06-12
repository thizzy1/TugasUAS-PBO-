package com.eanak.e.anak;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormTabelPasien extends JFrame {

    private final String jenisPasien;
    private JTable tabel;
    private DefaultTableModel modelTabel;
    private JTextField txtCari;
    private JLabel lblInfo;

    public FormTabelPasien(String jenisPasien) {
        this.jenisPasien = jenisPasien;
        initComponents();
        muatData();
    }

    private void initComponents() {
        boolean anak = jenisPasien.equals("anak");
        String judul = anak ? "Data Pasien Anak" : "Data Pasien Dewasa";
        Color warna  = anak ? new Color(52, 152, 219) : new Color(46, 204, 113);

        setTitle(judul + " — E-NAK");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(warna);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lblIkon = new JLabel(anak ? "👧" : "👨");
        lblIkon.setFont(new Font("SansSerif", Font.PLAIN, 28));

        JPanel teksH = new JPanel(new GridLayout(2, 1));
        teksH.setOpaque(false);
        JLabel h1 = new JLabel(judul);
        h1.setFont(new Font("SansSerif", Font.BOLD, 16));
        h1.setForeground(Color.WHITE);
        JLabel h2 = new JLabel("Data biodata pasien " + jenisPasien + " — E-NAK");
        h2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        h2.setForeground(new Color(220, 240, 255));
        teksH.add(h1); teksH.add(h2);
        header.add(lblIkon, BorderLayout.WEST);
        header.add(teksH,   BorderLayout.CENTER);

        // TOOLBAR
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(new Color(245, 248, 252));
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 235)),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JPanel cariPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cariPanel.setOpaque(false);
        JLabel lblCari = new JLabel("🔍  Cari:");
        lblCari.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtCari = new JTextField(22);
        txtCari.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JButton btnCari    = tombol("Cari", warna);
        JButton btnRefresh = tombol("↺ Refresh", new Color(100, 116, 139));
        cariPanel.add(lblCari); cariPanel.add(txtCari);
        cariPanel.add(btnCari); cariPanel.add(btnRefresh);

        lblInfo = new JLabel("Memuat data...");
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(120, 140, 160));

        toolbar.add(cariPanel, BorderLayout.WEST);
        toolbar.add(lblInfo,   BorderLayout.EAST);

        // TABEL
        modelTabel = new DefaultTableModel(kolomPasien(), 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(modelTabel);
        tabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabel.setRowHeight(30);
        tabel.setShowVerticalLines(false);
        tabel.setGridColor(new Color(235, 238, 245));
        tabel.setSelectionBackground(new Color(210, 235, 255));
        tabel.setIntercellSpacing(new Dimension(10, 0));
        tabel.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabel.getTableHeader().setBackground(warna);
        tabel.getTableHeader().setForeground(Color.WHITE);
        tabel.getTableHeader().setPreferredSize(new Dimension(0, 36));

        tabel.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelTabel);
        tabel.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        // FOOTER
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        footer.setBackground(new Color(245, 248, 252));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 235)));
        JButton btnTutup = tombol("✕  Tutup", new Color(231, 76, 60));
        footer.add(btnTutup);

        JPanel panelAtas = new JPanel(new BorderLayout());
        panelAtas.add(header,  BorderLayout.NORTH);
        panelAtas.add(toolbar, BorderLayout.SOUTH);
        add(panelAtas, BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
        add(footer,    BorderLayout.SOUTH);

        btnCari.addActionListener(e -> {
            String k = txtCari.getText().trim();
            sorter.setRowFilter(k.isEmpty() ? null : RowFilter.regexFilter("(?i)" + k));
        });
        txtCari.addActionListener(e -> btnCari.doClick());
        btnRefresh.addActionListener(e -> muatData());
        btnTutup.addActionListener(e -> dispose());
    }

    private String[] kolomPasien() {
        if (jenisPasien.equals("anak"))
            return new String[]{"ID","NRM","Nama","Tempat Lahir","Tgl Lahir",
                "L/P","Nama Ibu","Nama Ayah","Telp","Pendidikan","BB(kg)","TB(cm)","Alamat"};
        return new String[]{"ID","NRM","Nama","Tempat Lahir","Tgl Lahir",
            "L/P","Telp","Pendidikan","BB(kg)","TB(cm)","Alamat"};
    }

    public void muatData() {
        modelTabel.setRowCount(0);
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) { lblInfo.setText("Koneksi gagal."); return; }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM pasien_" + jenisPasien + " ORDER BY nama");
             ResultSet rs = ps.executeQuery()) {
            int n = rs.getMetaData().getColumnCount(), baris = 0;
            while (rs.next()) {
                Object[] row = new Object[n];
                for (int i = 1; i <= n; i++) row[i-1] = rs.getObject(i);
                modelTabel.addRow(row); baris++;
            }
            lblInfo.setText("Menampilkan " + baris + " pasien " + jenisPasien);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally { DatabaseConnection.closeConnection(conn); }
    }

    private JButton tombol(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setBackground(warna); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }
}
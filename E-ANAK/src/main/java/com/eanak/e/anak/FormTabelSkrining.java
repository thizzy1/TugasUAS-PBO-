package com.eanak.e.anak;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormTabelSkrining extends JFrame {

    private final String jenisPasien;
    private JTable tabel;
    private DefaultTableModel modelTabel;
    private JLabel lblInfo;

    public FormTabelSkrining(String jenisPasien) {
        this.jenisPasien = jenisPasien;
        initComponents();
        muatData();
    }

    private void initComponents() {
        boolean anak = jenisPasien.equals("anak");
        Color warna  = anak ? new Color(155, 89, 182) : new Color(230, 126, 34);
        String judul = "Penilaian Skrining " + (anak ? "Anak" : "Dewasa");

        setTitle(judul + " — E-NAK");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(warna);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lblIkon = new JLabel(anak ? "📋" : "📊");
        lblIkon.setFont(new Font("SansSerif", Font.PLAIN, 28));

        JPanel teksH = new JPanel(new GridLayout(2, 1));
        teksH.setOpaque(false);
        JLabel h1 = new JLabel(judul);
        h1.setFont(new Font("SansSerif", Font.BOLD, 16));
        h1.setForeground(Color.WHITE);
        JLabel h2 = new JLabel("Hasil penilaian skor gizi & interpretasi risiko");
        h2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        h2.setForeground(new Color(240, 240, 255));
        teksH.add(h1); teksH.add(h2);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        legend.setOpaque(false);
        legend.add(badge("Low Risk",    new Color(39, 174, 96)));
        legend.add(badge("Medium Risk", new Color(243, 156, 18)));
        legend.add(badge("High Risk",   new Color(231, 76, 60)));

        header.add(lblIkon, BorderLayout.WEST);
        header.add(teksH,   BorderLayout.CENTER);
        header.add(legend,  BorderLayout.EAST);

        // TABEL
        int kolInterp = anak ? 7 : 10;
        modelTabel = new DefaultTableModel(kolomSkrining(), 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(modelTabel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    Object val = getValueAt(row, kolInterp);
                    if (val != null) {
                        String v = val.toString();
                        if (v.equalsIgnoreCase("High Risk"))        c.setBackground(new Color(255, 235, 235));
                        else if (v.equalsIgnoreCase("Medium Risk")) c.setBackground(new Color(255, 248, 220));
                        else                                         c.setBackground(new Color(235, 255, 240));
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
                    }
                }
                return c;
            }
        };
        tabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabel.setRowHeight(30);
        tabel.setShowVerticalLines(false);
        tabel.setGridColor(new Color(235, 238, 245));
        tabel.setIntercellSpacing(new Dimension(10, 0));
        tabel.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabel.getTableHeader().setBackground(warna);
        tabel.getTableHeader().setForeground(Color.WHITE);
        tabel.getTableHeader().setPreferredSize(new Dimension(0, 36));

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // FOOTER
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(245, 248, 252));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 235)),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        lblInfo = new JLabel("Memuat data...");
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(120, 140, 160));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        JButton btnRefresh = tombol("↺ Refresh", new Color(100, 116, 139));
        JButton btnTutup   = tombol("✕  Tutup",  new Color(231, 76, 60));
        btnPanel.add(btnRefresh); btnPanel.add(btnTutup);
        footer.add(lblInfo,  BorderLayout.WEST);
        footer.add(btnPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);
        add(footer,  BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> muatData());
        btnTutup.addActionListener(e   -> dispose());
    }

    private String[] kolomSkrining() {
        if (jenisPasien.equals("anak"))
            return new String[]{"ID","Nama Pasien","NRM",
                "Skor A (Diagnosis)","Skor B (Asupan)","Skor C (Z-Score)",
                "Total","Interpretasi","Tindak Lanjut","Tanggal"};
        return new String[]{"ID","Nama Pasien","NRM",
            "Skor A","Skor B","Skor C","Skor D","Skor E","Skor F",
            "Total","Interpretasi","Tindak Lanjut","Tanggal"};
    }

    public void muatData() {
        modelTabel.setRowCount(0);
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) { lblInfo.setText("Koneksi gagal."); return; }
        String sql = jenisPasien.equals("anak")
            ? "SELECT s.id,p.nama,p.nrm,s.skor_a,s.skor_b,s.skor_c,s.total_skor,s.interpretasi,s.tindak_lanjut,s.tanggal FROM penilaian_skrining_anak s JOIN pasien_anak p ON s.pasien_id=p.id ORDER BY s.tanggal DESC"
            : "SELECT s.id,p.nama,p.nrm,s.skor_a,s.skor_b,s.skor_c,s.skor_d,s.skor_e,s.skor_f,s.total_skor,s.interpretasi,s.tindak_lanjut,s.tanggal FROM penilaian_skrining_dewasa s JOIN pasien_dewasa p ON s.pasien_id=p.id ORDER BY s.tanggal DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            int n = rs.getMetaData().getColumnCount(), baris = 0;
            while (rs.next()) {
                Object[] row = new Object[n];
                for (int i = 1; i <= n; i++) row[i-1] = rs.getObject(i);
                modelTabel.addRow(row); baris++;
            }
            lblInfo.setText("Menampilkan " + baris + " data skrining " + jenisPasien);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally { DatabaseConnection.closeConnection(conn); }
    }

    private JLabel badge(String teks, Color warna) {
        JLabel lbl = new JLabel("  " + teks + "  ");
        lbl.setOpaque(true); lbl.setBackground(warna); lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        return lbl;
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
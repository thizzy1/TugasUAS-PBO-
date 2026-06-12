package com.eanak.e.anak;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

public class FormMenuUtama extends JFrame {

    private JLabel lblStatus;

    public FormMenuUtama() {
        initComponents();
        cekKoneksi();
    }

    private void initComponents() {
        setTitle("E-NAK — Aplikasi Skrining Gizi Anak");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // ===================== SIDEBAR KIRI =====================
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(18, 52, 86));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        JLabel lblLogo = new JLabel("🍎", SwingConstants.CENTER);
        lblLogo.setFont(new Font("SansSerif", Font.PLAIN, 52));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNama = new JLabel("E-NAK");
        lblNama.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblNama.setForeground(Color.WHITE);
        lblNama.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTagline = new JLabel("<html><center>Skrining Gizi<br>Anak Digital</center></html>");
        lblTagline.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTagline.setForeground(new Color(160, 200, 230));
        lblTagline.setHorizontalAlignment(SwingConstants.CENTER);
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(160, 1));
        sep.setForeground(new Color(60, 100, 140));

        JLabel lblVersi = new JLabel("v1.0  |  Jurnal Wiyata 2023");
        lblVersi.setFont(new Font("SansSerif", Font.ITALIC, 10));
        lblVersi.setForeground(new Color(120, 160, 200));
        lblVersi.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblStatus = new JLabel("● Memeriksa koneksi...");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(200, 200, 200));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(lblNama);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(lblTagline);
        sidebar.add(Box.createVerticalStrut(16));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(16));
        sidebar.add(lblVersi);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(lblStatus);

        // ===================== PANEL KANAN =====================
        JPanel panelKanan = new JPanel(new BorderLayout());
        panelKanan.setBackground(new Color(245, 248, 252));

        JPanel panelJudul = new JPanel(new BorderLayout());
        panelJudul.setBackground(new Color(245, 248, 252));
        panelJudul.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 25));

        JLabel lblJudul = new JLabel("Menu Utama");
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblJudul.setForeground(new Color(30, 60, 90));

        JLabel lblSubjudul = new JLabel("Pilih menu untuk mulai bekerja");
        lblSubjudul.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubjudul.setForeground(new Color(120, 140, 160));

        panelJudul.add(lblJudul,    BorderLayout.NORTH);
        panelJudul.add(lblSubjudul, BorderLayout.SOUTH);

        JPanel panelGrid = new JPanel(new GridLayout(3, 2, 14, 14));
        panelGrid.setBackground(new Color(245, 248, 252));
        panelGrid.setBorder(BorderFactory.createEmptyBorder(10, 25, 15, 25));

        panelGrid.add(buatKartu("👧  Pasien Anak",
            "Lihat data biodata\npasien anak",
            new Color(52, 152, 219), e -> new FormTabelPasien("anak").setVisible(true)));

        panelGrid.add(buatKartu("👨  Pasien Dewasa",
            "Lihat data biodata\npasien dewasa",
            new Color(46, 204, 113), e -> new FormTabelPasien("dewasa").setVisible(true)));

        panelGrid.add(buatKartu("📋  Skrining Anak",
            "Hasil penilaian\nskrining gizi anak",
            new Color(155, 89, 182), e -> new FormTabelSkrining("anak").setVisible(true)));

        panelGrid.add(buatKartu("📊  Skrining Dewasa",
            "Hasil penilaian\nskrining gizi dewasa",
            new Color(230, 126, 34), e -> new FormTabelSkrining("dewasa").setVisible(true)));

        panelGrid.add(buatKartu("🏆  Hasil Asesmen",
            "Rekap skor akhir &\ninterpretasi semua pasien",
            new Color(26, 188, 156), e -> new FormTabelHasil().setVisible(true)));

        panelGrid.add(buatKartu("🚪  Keluar",
            "Tutup aplikasi\nE-NAK",
            new Color(231, 76, 60), e -> {
                int ok = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin keluar?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (ok == JOptionPane.YES_OPTION) System.exit(0);
            }));

        panelKanan.add(panelJudul, BorderLayout.NORTH);
        panelKanan.add(panelGrid,  BorderLayout.CENTER);

        add(sidebar,    BorderLayout.WEST);
        add(panelKanan, BorderLayout.CENTER);
    }

    private JPanel buatKartu(String judul, String deskripsi, Color warna, ActionListener aksi) {
        JPanel kartu = new JPanel(new BorderLayout(0, 6));
        kartu.setBackground(Color.WHITE);
        kartu.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 225, 235), 1, true),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        kartu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel strip = new JPanel();
        strip.setBackground(warna);
        strip.setPreferredSize(new Dimension(5, 0));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblJudul.setForeground(new Color(30, 50, 70));

        JLabel lblDesc = new JLabel("<html>" + deskripsi.replace("\n", "<br>") + "</html>");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(130, 150, 170));

        JPanel teks = new JPanel(new BorderLayout(0, 3));
        teks.setBackground(Color.WHITE);
        teks.add(lblJudul, BorderLayout.NORTH);
        teks.add(lblDesc,  BorderLayout.CENTER);

        kartu.add(strip, BorderLayout.WEST);
        kartu.add(teks,  BorderLayout.CENTER);

        kartu.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                kartu.setBackground(new Color(240, 248, 255));
                teks.setBackground(new Color(240, 248, 255));
                kartu.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(warna, 2, true),
                    BorderFactory.createEmptyBorder(15, 17, 15, 17)));
            }
            @Override public void mouseExited(MouseEvent e) {
                kartu.setBackground(Color.WHITE);
                teks.setBackground(Color.WHITE);
                kartu.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(220, 225, 235), 1, true),
                    BorderFactory.createEmptyBorder(16, 18, 16, 18)));
            }
            @Override public void mouseClicked(MouseEvent e) { aksi.actionPerformed(null); }
        });

        return kartu;
    }

    private void cekKoneksi() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            lblStatus.setText("● Terhubung ke database");
            lblStatus.setForeground(new Color(80, 220, 120));
            DatabaseConnection.closeConnection(conn);
        } else {
            lblStatus.setText("● Database tidak terhubung");
            lblStatus.setForeground(new Color(255, 100, 100));
        }
    }

    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new FormMenuUtama().setVisible(true));
    }
}
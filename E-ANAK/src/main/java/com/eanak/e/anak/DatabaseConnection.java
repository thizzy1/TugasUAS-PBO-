package com.eanak.e.anak;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {

    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "enak_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
        + "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Driver MySQL tidak ditemukan!\nPastikan mysql-connector sudah ada di Dependencies.",
                "Error Driver", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal terhubung ke database!\n" + e.getMessage(),
                "Error Koneksi", JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try { conn.close(); }
            catch (SQLException e) { System.err.println("Gagal tutup koneksi: " + e.getMessage()); }
        }
    }
}
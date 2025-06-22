package main;

import View.RegisterPage;
import java.sql.SQLException;

/**
 * Kelas utama untuk menjalankan aplikasi Barang Parabotan.
 * Program akan membuka koneksi ke database dan menampilkan halaman registrasi pengguna.
 */
public class BarangParabotan {

    /**
     * Metode utama (entry point) aplikasi.
     *
     * @param args argumen baris perintah (tidak digunakan)
     * @throws SQLException jika koneksi ke database gagal
     */
    public static void main(String[] args) throws SQLException {
        // Menampilkan halaman registrasi
        RegisterPage hasanParabot = new RegisterPage("", "");
        hasanParabot.setVisible(true);
    }
}

package Class;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Kelas utilitas untuk mengelola koneksi ke database MySQL.
 */
public class database {

    // URL koneksi ke database, termasuk pengaturan SSL dan zona waktu
    private static final String URL = "jdbc:mysql://localhost:3306/hasanbosparabotan?useSSL=false&serverTimezone=UTC";
    
    // Username dan password untuk koneksi database
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Mengembalikan objek {@link Connection} untuk berkomunikasi dengan database.
     *
     * @return objek Connection yang telah terkoneksi dengan database
     * @throws SQLException jika terjadi kesalahan saat koneksi atau driver tidak ditemukan
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Memuat driver JDBC untuk MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Mengembalikan koneksi ke database
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            // Menangani error jika driver tidak ditemukan
            System.err.println("Driver JDBC tidak ditemukan.");
            throw new SQLException("Driver JDBC tidak ditemukan.");
        } catch (SQLException e) {
            // Menangani error jika gagal koneksi
            System.err.println("Gagal terkoneksi ke database: " + e.getMessage());
            throw e;
        }
    }
}

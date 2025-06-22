package Class;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Kelas Userlog digunakan untuk menampilkan riwayat log pembelian
 * berdasarkan username pengguna tertentu.
 */
public class Userlog {
    /**
     * Nama pengguna.
     */
    public String nama;

    /**
     * Konstruktor Userlog dengan parameter nama.
     *
     * @param nama nama pengguna
     */
    public Userlog(String nama) {
        this.nama = nama;
    }

    /**
     * Menampilkan log pembelian user ke dalam JTable.
     *
     * @param conn koneksi database
     * @param jTable komponen tabel untuk menampilkan data
     * @param username nama pengguna yang akan ditampilkan log pembeliannya
     */
    public void tampilkanLogPembelian(Connection conn, JTable jTable, String username) {
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][] {},
            new String[] {"ID Transaksi", "Nama Barang", "Jumlah", "Harga per Item", "Total Harga", "Tanggal"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        DefaultTableModel tb = (DefaultTableModel) jTable.getModel();
        tb.setRowCount(0);
        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.getTableHeader().setResizingAllowed(false);
        jTable.setRowSelectionAllowed(true);
        jTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        String sql = "SELECT t.idtransaksi, b.namabarang, t.jumlah, b.harga, (t.jumlah * b.harga) AS total, t.tanggal " +
                     "FROM transaksi t " +
                     "JOIN barang b ON t.idbarang = b.idbarang " +
                     "WHERE t.username = ? " +
                     "ORDER BY t.tanggal DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("idtransaksi");
                String namaBarang = rs.getString("namabarang");
                int jumlah = rs.getInt("jumlah");
                int harga = rs.getInt("harga");
                int total = rs.getInt("total");
                String tanggal = rs.getString("tanggal");

                tb.addRow(new Object[]{id, namaBarang, jumlah, harga, total, tanggal});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data log pembelian: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package Class;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Statement;

/**
 * Interface untuk operasi manajemen barang.
 */
interface BarangInterface {

    /**
     * Menampilkan semua data barang ke dalam tabel.
     *
     * @param conn koneksi database
     * @param jTable1 komponen tabel untuk menampilkan data
     */
    void showBarang(Connection conn, JTable jTable1);

    /**
     * Mengedit data barang yang dipilih dari tabel.
     *
     * @param conn koneksi database
     * @param table komponen tabel berisi data barang
     */
    void editBarang(Connection conn, JTable table);

    /**
     * Menghapus barang dari database berdasarkan baris yang dipilih.
     *
     * @param conn koneksi database
     * @param table komponen tabel berisi data barang
     */
    void hapusBarang(Connection conn, JTable table);

    /**
     * Menambahkan barang baru ke database.
     *
     * @param conn koneksi database
     * @param table komponen tabel untuk refresh setelah penambahan
     */
    void tambahBarang(Connection conn, JTable table);

    /**
     * default method untuk menampilkan informasi interface.
     */
    default void tampilkanInfo() {
        System.out.println("Ini adalah interface untuk manajemen barang.");
    }

    /**
     * Static method untuk mencetak header data barang ke konsol.
     */
    static void cetakHeader() {
        System.out.println("== Data Barang ==");
        System.out.println("ID | Nama | Stok | Harga");
    }
}

/**
 * Kelas Barang untuk mengelola data barang dan transaksi pembelian.
 */
public class Barang implements BarangInterface {
    public String barang;

    /**
     * Konstruktor dengan parameter nama barang.
     *
     * @param namaBarang nama barang
     */
    public Barang(String namaBarang) {
        this.barang = namaBarang;
    }

    /**
     * Konstruktor tanpa parameter.
     */
    public Barang() {}

    /**
     * Menampilkan seluruh data barang ke dalam JTable.
     *
     * @param conn koneksi database
     * @param jTable1 tabel tujuan
     */
    @Override
    public void showBarang(Connection conn, JTable jTable1) {
        jTable1.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Barang", "Nama Barang", "Stok Barang", "Harga"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        DefaultTableModel tb = (DefaultTableModel) jTable1.getModel();
        tb.setRowCount(0);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.setRowSelectionAllowed(true);
        jTable1.setColumnSelectionAllowed(false);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        String sql = "SELECT idbarang, namabarang, stockbarang, harga FROM barang";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idbarang");
                String nama = rs.getString("namabarang");
                int stok = rs.getInt("stockbarang");
                int harga = rs.getInt("harga");

                tb.addRow(new Object[]{id, nama, stok, harga});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data barang: " + e.getMessage());
        }
    }

    /**
     * Mengedit data barang berdasarkan input dari user melalui dialog.
     *
     * @param conn koneksi database
     * @param table tabel barang yang sedang ditampilkan
     */
    @Override
    public void editBarang(Connection conn, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null,
                    "Pilih satu baris data terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idBarang = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
        String namaBarang = table.getValueAt(selectedRow, 1).toString();
        int stokBarang = Integer.parseInt(table.getValueAt(selectedRow, 2).toString());
        double hargaBarang = Double.parseDouble(table.getValueAt(selectedRow, 3).toString());

        String newNama = JOptionPane.showInputDialog(null, "Nama Barang:", namaBarang);
        if (newNama == null) return;

        String newStokStr = JOptionPane.showInputDialog(null, "Stok Barang:", stokBarang);
        if (newStokStr == null) return;

        String newHargaStr = JOptionPane.showInputDialog(null, "Harga Barang:", hargaBarang);
        if (newHargaStr == null) return;

        try {
            int newStok = Integer.parseInt(newStokStr);
            double newHarga = Double.parseDouble(newHargaStr);

            String query = "UPDATE barang SET namabarang=?, stockbarang=?, harga=? WHERE idbarang=?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, newNama);
                ps.setInt(2, newStok);
                ps.setDouble(3, newHarga);
                ps.setInt(4, idBarang);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(null, "Data berhasil diperbarui.");
                    showBarang(conn, table);
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal memperbarui data.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Input stok atau harga tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Kesalahan database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Menghapus data barang dari database berdasarkan pilihan user.
     *
     * @param conn koneksi database
     * @param table tabel barang yang sedang ditampilkan
     */
    @Override
    public void hapusBarang(Connection conn, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Pilih salah satu baris untuk dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Apakah kamu yakin ingin menghapus data ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int idBarang = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());

            String query = "DELETE FROM barang WHERE idbarang=?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idBarang);
                int result = ps.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus.");
                    showBarang(conn, table);
                } else {
                    JOptionPane.showMessageDialog(null, "Data gagal dihapus.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Kesalahan database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Menambahkan data barang baru ke database berdasarkan input user.
     *
     * @param conn koneksi database
     * @param table tabel barang untuk direfresh
     */
    @Override
    public void tambahBarang(Connection conn, JTable table) {
        String nama = JOptionPane.showInputDialog(null, "Masukkan Nama Barang:");
        if (nama == null || nama.trim().isEmpty()) return;

        try {
            // Cek apakah nama barang sudah ada (case-insensitive)
            String cekNamaQuery = "SELECT COUNT(*) FROM barang WHERE LOWER(namabarang) = LOWER(?)";
            try (PreparedStatement ps = conn.prepareStatement(cekNamaQuery)) {
                ps.setString(1, nama.trim());
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "Nama Barang sudah digunakan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Dapatkan ID baru dari nilai maksimum saat ini + 1
            int idBaru = 1;
            String getMaxIdQuery = "SELECT MAX(idbarang) FROM barang";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(getMaxIdQuery)) {
                if (rs.next()) {
                    idBaru = rs.getInt(1) + 1;
                }
            }

            // Set stok dan harga default
            int stokDefault = 0;
            double hargaDefault = 0;

            // Insert barang baru
            String insertQuery = "INSERT INTO barang (idbarang, namabarang, stockbarang, harga) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setInt(1, idBaru);
                ps.setString(2, nama.trim());
                ps.setInt(3, stokDefault);
                ps.setDouble(4, hargaDefault);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(null, "Barang berhasil ditambahkan.");
                    showBarang(conn, table);
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal menambahkan barang.");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Kesalahan database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Digunakan oleh user untuk membeli barang dari tabel.
     *
     * @param conn koneksi database
     * @param table tabel barang
     * @param username nama user yang melakukan pembelian
     */
    public void beliBarang(Connection conn, JTable table, String username) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Pilih barang yang ingin dibeli.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idBarang = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
        String namaBarang = table.getValueAt(selectedRow, 1).toString();
        int stok = Integer.parseInt(table.getValueAt(selectedRow, 2).toString());
        double harga = Double.parseDouble(table.getValueAt(selectedRow, 3).toString());

        String jumlahStr = JOptionPane.showInputDialog(null, "Masukkan jumlah yang ingin dibeli:");
        if (jumlahStr == null) return;

        try {
            int jumlahBeli = Integer.parseInt(jumlahStr);
            if (jumlahBeli <= 0) {
                JOptionPane.showMessageDialog(null, "Jumlah harus lebih dari 0.");
                return;
            }

            if (jumlahBeli > stok) {
                JOptionPane.showMessageDialog(null, "Stok tidak mencukupi.");
                return;
            }

            double totalHarga = jumlahBeli * harga;

            int konfirmasi = JOptionPane.showConfirmDialog(null,
                    "Konfirmasi pembelian:\n"
                            + "Barang     : " + namaBarang + "\n"
                            + "Jumlah     : " + jumlahBeli + "\n"
                            + "Harga/pcs  : Rp" + harga + "\n"
                            + "Total Bayar: Rp" + totalHarga + "\n\n"
                            + "Lanjutkan pembelian?",
                    "Konfirmasi Pembelian", JOptionPane.YES_NO_OPTION);

            if (konfirmasi != JOptionPane.YES_OPTION) return;

            conn.setAutoCommit(false);

            String updateStok = "UPDATE barang SET stockbarang = stockbarang - ? WHERE idbarang = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(updateStok)) {
                ps1.setInt(1, jumlahBeli);
                ps1.setInt(2, idBarang);
                ps1.executeUpdate();
            }

            String insertTransaksi = "INSERT INTO transaksi (idbarang, username, jumlah, tanggal) VALUES (?, ?, ?, NOW())";
            try (PreparedStatement ps2 = conn.prepareStatement(insertTransaksi)) {
                ps2.setInt(1, idBarang);
                ps2.setString(2, username);
                ps2.setInt(3, jumlahBeli);
                ps2.executeUpdate();
            }

            conn.commit();

            JOptionPane.showMessageDialog(null,
                    "Pembelian berhasil!\n\n"
                            + "Barang     : " + namaBarang + "\n"
                            + "Jumlah     : " + jumlahBeli + "\n"
                            + "Total Bayar: Rp" + totalHarga,
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

            showBarang(conn, table);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Masukkan jumlah yang valid.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Gagal rollback: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(null, "Kesalahan saat menyimpan pembelian: " + e.getMessage());
        }
    }

    /**
     * Menampilkan log transaksi pembelian yang dilakukan oleh user.
     *
     * @param conn koneksi database
     * @param table tabel tujuan log
     * @param username nama user
     */
    public void showLogUser(Connection conn, JTable table, String username) {
        table.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Transaksi", "Nama Barang", "Jumlah", "Harga/Item", "Total Harga", "Tanggal"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        DefaultTableModel tb = (DefaultTableModel) table.getModel();
        tb.setRowCount(0);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        String sql = "SELECT t.idtransaksi, b.namabarang, t.jumlah, b.harga, (t.jumlah * b.harga) AS total, t.tanggal " +
                "FROM transaksi t " +
                "JOIN barang b ON t.idbarang = b.idbarang " +
                "WHERE t.username = ? " +
                "ORDER BY t.tanggal DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("idtransaksi");
                String nama = rs.getString("namabarang");
                int jumlah = rs.getInt("jumlah");
                double harga = rs.getDouble("harga");
                double total = rs.getDouble("total");
                String tanggal = rs.getString("tanggal");

                tb.addRow(new Object[]{id, nama, jumlah, harga, total, tanggal});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil log pembelian: " + e.getMessage());
        }
    }
}

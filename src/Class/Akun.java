package Class;

import View.BerandaAdmin;
import View.BerandaUser;
import View.LoginPage;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Kelas superclass Pengguna yang merepresentasikan informasi dasar dari pengguna.
 */
class Pengguna {
    protected String username;
    protected String role;

    /**
     * Menampilkan informasi pengguna ke konsol.
     */
    public void tampilkanInfo() {
        System.out.println("Username: " + username);
        System.out.println("Role: " + role);
    }
}

/**
 * Kelas Akun mewarisi dari Pengguna dan mencakup fitur untuk login, registrasi, serta CRUD akun.
 */
public class Akun extends Pengguna {
    private String password;

    /**
     * Konstruktor Akun dengan parameter lengkap.
     *
     * @param username nama pengguna
     * @param password kata sandi pengguna
     * @param role     peran pengguna (admin/user)
     */
    public Akun(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Konstruktor Akun hanya dengan username.
     *
     * @param username nama pengguna
     */
    public Akun(String username) {
        this.username = username;
    }

    /**
     * Mendapatkan nama pengguna.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Mengatur nama pengguna.
     *
     * @param username nama pengguna baru
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Mendapatkan kata sandi.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Mengatur kata sandi.
     *
     * @param password kata sandi baru
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Mendapatkan peran pengguna.
     *
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * Mengatur peran pengguna.
     *
     * @param role peran baru (admin/user)
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Mendaftarkan pengguna baru ke database.
     *
     * @return true jika registrasi berhasil, false jika gagal
     */
    public boolean registerUser() {
        String checkSql = "SELECT COUNT(*) FROM akun WHERE nama = ?";
        String insertSql = "INSERT INTO akun (nama, password, role) VALUES (?, ?, ?)";
        boolean registerSuccess = false;

        try (Connection conn = database.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Registrasi gagal: Nama pengguna sudah terdaftar.", "Gagal", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setString(3, role);

                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Registrasi berhasil! Akun telah dibuat.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    registerSuccess = true;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Registrasi gagal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return registerSuccess;
    }

    /**
     * Melakukan proses login berdasarkan username dan password.
     *
     * @param a objek pemanggil, digunakan untuk menyembunyikan form login jika berhasil
     * @return true jika login berhasil, false jika gagal
     */
    public boolean loginUser(Object a) {
        String loginSql = "SELECT * FROM akun WHERE nama = ? AND password = ?";
        boolean loginSuccess = false;

        try (Connection conn = database.getConnection();
             PreparedStatement loginStmt = conn.prepareStatement(loginSql)) {

            loginStmt.setString(1, username);
            loginStmt.setString(2, password);

            ResultSet rs = loginStmt.executeQuery();
            if (rs.next()) {
                role = rs.getString("role");

                if (role.equals("user")) {
                    JOptionPane.showMessageDialog(null, "Login berhasil! Selamat datang user, " + username, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loginSuccess = true;
                    if (a instanceof LoginPage) {
                        ((LoginPage) a).setVisible(false);
                    }
                    BerandaUser user = new BerandaUser(username);
                    user.setVisible(true);
                } else if (role.equals("admin")) {
                    JOptionPane.showMessageDialog(null, "Login berhasil! Selamat datang admin, " + username, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    loginSuccess = true;
                    if (a instanceof LoginPage) {
                        ((LoginPage) a).setVisible(false);
                    }
                    BerandaAdmin admin = new BerandaAdmin(username);
                    admin.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Login gagal: Username atau password salah.", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Login gagal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return loginSuccess;
    }

    /**
     * Menampilkan informasi lengkap akun termasuk password (override).
     */
    @Override
    public void tampilkanInfo() {
        super.tampilkanInfo();
        System.out.println("Password: " + password);
    }

    /**
     * Menampilkan data semua akun ke dalam JTable.
     *
     * @param conn    koneksi database
     * @param jTable1 tabel untuk menampilkan data
     */
    public void showAkun(Connection conn, JTable jTable1) {
        jTable1.setModel(new DefaultTableModel(new Object[][] {},
                new String[]{"ID Akun", "Nama", "Role", "Tanggal Register"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        DefaultTableModel tb = (DefaultTableModel) jTable1.getModel();
        tb.setRowCount(0);

        String sql = "SELECT id, nama, role, tanggal_dibuat FROM akun";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tb.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("role"),
                        rs.getString("tanggal_dibuat")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data akun: " + e.getMessage());
        }
    }

    /**
     * Mengedit data nama dan role dari akun yang dipilih pada JTable.
     *
     * @param conn  koneksi database
     * @param table tabel yang berisi data akun
     */
    public void editAkun(Connection conn, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Pilih satu baris data untuk diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAkun = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
        String namaLama = table.getValueAt(selectedRow, 1).toString();
        String roleLama = table.getValueAt(selectedRow, 2).toString();

        String namaBaru = JOptionPane.showInputDialog(null, "Edit Nama:", namaLama);
        if (namaBaru == null) return;
        String roleBaru = JOptionPane.showInputDialog(null, "Edit Role:", roleLama);
        if (roleBaru == null) return;

        String sql = "UPDATE akun SET nama = ?, role = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaBaru.trim());
            ps.setString(2, roleBaru.trim());
            ps.setInt(3, idAkun);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Data akun berhasil diperbarui.");
                showAkun(conn, table);
            } else {
                JOptionPane.showMessageDialog(null, "Gagal memperbarui data akun.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Kesalahan database: " + e.getMessage());
        }
    }

    /**
     * Menghapus akun yang dipilih dari database dan memperbarui JTable.
     *
     * @param conn  koneksi database
     * @param table tabel yang berisi data akun
     */
    public void hapusAkun(Connection conn, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Pilih salah satu akun untuk dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAkun = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
        String namaAkun = table.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus akun \"" + namaAkun + "\"?",
                "Konfirmasi Hapus Akun", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM akun WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAkun);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Akun berhasil dihapus.");
                showAkun(conn, table);
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus akun.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Kesalahan database: " + e.getMessage());
        }
    }
}

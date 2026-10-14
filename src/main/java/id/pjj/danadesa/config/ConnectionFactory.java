package id.pjj.danadesa.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Membuat koneksi JDBC baru. Pemanggil wajib menutupnya (try-with-resources). */
public class ConnectionFactory {

    private final DatabaseConfig config;

    public ConnectionFactory(DatabaseConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }

    /** Dipanggil saat aplikasi mulai, supaya salah konfigurasi langsung ketahuan. */
    public void ujiKoneksi() throws SQLException {
        try (Connection c = getConnection()) {
            if (!c.isValid(3)) {
                throw new SQLException("Koneksi database tidak valid.");
            }
        }
    }
}

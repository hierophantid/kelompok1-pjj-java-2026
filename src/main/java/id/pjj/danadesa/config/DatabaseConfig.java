package id.pjj.danadesa.config;

import id.pjj.danadesa.exception.KonfigurasiException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Membaca alamat, user, dan password database dari db.properties.
 * Urutan pencarian:
 * 1. path di system property -Ddanadesa.config=...
 * 2. file db.properties di folder kerja (folder proyek saat Run di NetBeans)
 * 3. db.properties di classpath (src/main/resources)
 */
public final class DatabaseConfig {

    public static final String NAMA_FILE = "db.properties";

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig muat() throws KonfigurasiException {
        Properties p = new Properties();
        String pathKustom = System.getProperty("danadesa.config");
        Path lokal = Path.of(pathKustom != null ? pathKustom : NAMA_FILE);
        if (pathKustom != null && !Files.isRegularFile(lokal)) {
            // Path yang ditulis eksplisit harus ada; jangan diam-diam pakai file lain.
            throw new KonfigurasiException("File konfigurasi " + lokal.toAbsolutePath() + " tidak ditemukan.");
        }

        try {
            if (Files.isRegularFile(lokal)) {
                try (Reader r = Files.newBufferedReader(lokal, StandardCharsets.UTF_8)) {
                    p.load(r);
                }
            } else {
                try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + NAMA_FILE)) {
                    if (in == null) {
                        throw new KonfigurasiException("File " + NAMA_FILE + " tidak ditemukan.\n"
                                + "Salin db.properties.example menjadi db.properties di folder proyek,\n"
                                + "lalu isi db.url, db.user, dan db.password.");
                    }
                    p.load(in);
                }
            }
        } catch (IOException e) {
            throw new KonfigurasiException("Gagal membaca " + NAMA_FILE + ": " + e.getMessage(), e);
        }
        return dariProperties(p);
    }

    static DatabaseConfig dariProperties(Properties p) throws KonfigurasiException {
        String url = p.getProperty("db.url", "").trim();
        String user = p.getProperty("db.user", "").trim();
        String password = p.getProperty("db.password");
        if (url.isEmpty() || user.isEmpty() || password == null) {
            throw new KonfigurasiException(NAMA_FILE
                    + " belum lengkap. Wajib ada: db.url, db.user, db.password.");
        }
        return new DatabaseConfig(url, user, password);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}

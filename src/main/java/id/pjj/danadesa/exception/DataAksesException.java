package id.pjj.danadesa.exception;

import java.sql.SQLException;

/**
 * Membungkus SQLException menjadi pesan yang bisa dipahami pengguna.
 * Unchecked supaya interface Repository tetap bersih.
 */
public class DataAksesException extends RuntimeException {

    // Kode error MySQL/MariaDB
    private static final int DUPLIKAT = 1062;
    private static final int INDUK_MASIH_DIPAKAI = 1451;
    private static final int INDUK_TIDAK_ADA = 1452;

    public DataAksesException(String pesan, Throwable sebab) {
        super(pesan, sebab);
    }

    public static DataAksesException dari(SQLException e) {
        String pesan = switch (e.getErrorCode()) {
            case DUPLIKAT ->
                "Data dengan kode yang sama sudah ada.";
            case INDUK_MASIH_DIPAKAI ->
                "Data tidak bisa dihapus karena masih dipakai oleh data lain.";
            case INDUK_TIDAK_ADA ->
                "Data induk (relasi) tidak ditemukan.";
            default -> {
                String state = e.getSQLState();
                if (state != null && state.startsWith("08")) {
                    yield "Koneksi ke database gagal. Periksa server database dan isi db.properties.";
                }
                yield "Terjadi kesalahan database: " + e.getMessage();
            }
        };
        return new DataAksesException(pesan, e);
    }
}

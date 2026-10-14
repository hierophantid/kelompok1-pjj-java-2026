package id.pjj.danadesa.exception;

/** Input pengguna tidak valid: kosong, bukan angka, format tanggal salah, dll. */
public class ValidasiException extends AplikasiException {

    public ValidasiException(String pesan) {
        super(pesan);
    }
}

package id.pjj.danadesa.exception;

/** Induk semua exception aplikasi yang pesannya layak ditampilkan ke pengguna. */
public class AplikasiException extends Exception {

    public AplikasiException(String pesan) {
        super(pesan);
    }

    public AplikasiException(String pesan, Throwable sebab) {
        super(pesan, sebab);
    }
}

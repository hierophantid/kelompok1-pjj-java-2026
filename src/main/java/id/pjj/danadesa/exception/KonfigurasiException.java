package id.pjj.danadesa.exception;

/** File db.properties tidak ada atau isinya tidak lengkap. */
public class KonfigurasiException extends AplikasiException {

    public KonfigurasiException(String pesan) {
        super(pesan);
    }

    public KonfigurasiException(String pesan, Throwable sebab) {
        super(pesan, sebab);
    }
}

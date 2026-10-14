package id.pjj.danadesa.exception;

/** Input valid secara format, tetapi melanggar aturan bisnis penyaluran dana desa. */
public class AturanBisnisException extends AplikasiException {

    public AturanBisnisException(String pesan) {
        super(pesan);
    }
}

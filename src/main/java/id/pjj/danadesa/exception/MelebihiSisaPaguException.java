package id.pjj.danadesa.exception;

import id.pjj.danadesa.util.Format;
import java.math.BigDecimal;

/** Custom exception: nilai penyaluran lebih besar dari sisa pagu desa. */
public class MelebihiSisaPaguException extends AturanBisnisException {

    private final BigDecimal nilai;
    private final BigDecimal sisaPagu;

    public MelebihiSisaPaguException(BigDecimal nilai, BigDecimal sisaPagu) {
        super("Nilai penyaluran " + Format.rupiah(nilai)
                + " melebihi sisa pagu desa " + Format.rupiah(sisaPagu) + ".");
        this.nilai = nilai;
        this.sisaPagu = sisaPagu;
    }

    public BigDecimal getNilai() {
        return nilai;
    }

    public BigDecimal getSisaPagu() {
        return sisaPagu;
    }
}

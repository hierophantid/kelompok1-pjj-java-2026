package id.pjj.danadesa.service.aturan;

import id.pjj.danadesa.exception.MelebihiSisaPaguException;
import java.math.BigDecimal;

/** Nilai penyaluran tidak boleh melebihi sisa pagu desa. */
public class AturanSisaPagu implements AturanPenyaluran {

    @Override
    public void periksa(KonteksPenyaluran k) throws MelebihiSisaPaguException {
        BigDecimal sisa = k.sisaPagu();
        BigDecimal nilai = k.baru().getNilai();
        if (nilai.compareTo(sisa) > 0) {
            throw new MelebihiSisaPaguException(nilai, sisa);
        }
    }
}

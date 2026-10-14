package id.pjj.danadesa.service.aturan;

import id.pjj.danadesa.exception.AturanBisnisException;
import java.util.List;

/** Menjalankan daftar aturan secara berurutan; berhenti di pelanggaran pertama. */
public class ValidatorPenyaluran {

    private final List<AturanPenyaluran> daftarAturan;

    public ValidatorPenyaluran(List<AturanPenyaluran> daftarAturan) {
        this.daftarAturan = List.copyOf(daftarAturan);
    }

    public static ValidatorPenyaluran standar() {
        return new ValidatorPenyaluran(List.of(
                new AturanTahapBelumAda(),
                new AturanUrutanTahap(),
                new AturanTanggal(),
                new AturanSisaPagu()));
    }

    public void periksa(KonteksPenyaluran konteks) throws AturanBisnisException {
        for (AturanPenyaluran aturan : daftarAturan) {
            aturan.periksa(konteks);   // pemanggilan polimorfik
        }
    }
}

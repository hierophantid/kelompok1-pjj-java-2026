package id.pjj.danadesa.service.aturan;

import id.pjj.danadesa.exception.AturanBisnisException;
import id.pjj.danadesa.util.Format;

/** Satu desa hanya boleh punya satu penyaluran per tahap. */
public class AturanTahapBelumAda implements AturanPenyaluran {

    @Override
    public void periksa(KonteksPenyaluran k) throws AturanBisnisException {
        var tahap = k.baru().getTahap();
        var sudahAda = k.cariTahap(tahap);
        if (sudahAda.isPresent()) {
            throw new AturanBisnisException(tahap.getLabel() + " untuk desa " + k.desa().getNama()
                    + " sudah disalurkan pada " + Format.tanggal(sudahAda.get().getTanggal()) + ".");
        }
    }
}

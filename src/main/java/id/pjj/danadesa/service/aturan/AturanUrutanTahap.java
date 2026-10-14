package id.pjj.danadesa.service.aturan;

import id.pjj.danadesa.exception.AturanBisnisException;
import id.pjj.danadesa.model.Tahap;
import java.util.Optional;

/** Tahap II hanya boleh setelah Tahap I, Tahap III setelah Tahap II. */
public class AturanUrutanTahap implements AturanPenyaluran {

    @Override
    public void periksa(KonteksPenyaluran k) throws AturanBisnisException {
        Tahap tahap = k.baru().getTahap();
        Optional<Tahap> sebelumnya = tahap.sebelumnya();
        if (sebelumnya.isPresent() && k.cariTahap(sebelumnya.get()).isEmpty()) {
            throw new AturanBisnisException(tahap.getLabel() + " belum bisa dicatat karena "
                    + sebelumnya.get().getLabel() + " belum disalurkan.");
        }
    }
}

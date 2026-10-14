package id.pjj.danadesa.service.aturan;

import id.pjj.danadesa.exception.AturanBisnisException;
import id.pjj.danadesa.model.Penyaluran;
import id.pjj.danadesa.util.Format;
import java.time.LocalDate;

/** Tanggal tidak boleh di masa depan dan tidak boleh mendahului tahap sebelumnya. */
public class AturanTanggal implements AturanPenyaluran {

    @Override
    public void periksa(KonteksPenyaluran k) throws AturanBisnisException {
        LocalDate tanggal = k.baru().getTanggal();
        if (tanggal.isAfter(k.hariIni())) {
            throw new AturanBisnisException("Tanggal penyaluran " + Format.tanggal(tanggal)
                    + " tidak boleh melewati hari ini.");
        }
        var sebelumnya = k.baru().getTahap().sebelumnya().flatMap(k::cariTahap);
        if (sebelumnya.isPresent()) {
            Penyaluran p = sebelumnya.get();
            if (tanggal.isBefore(p.getTanggal())) {
                throw new AturanBisnisException("Tanggal " + k.baru().getTahap().getLabel()
                        + " tidak boleh sebelum tanggal " + p.getTahap().getLabel()
                        + " (" + Format.tanggal(p.getTanggal()) + ").");
            }
        }
    }
}

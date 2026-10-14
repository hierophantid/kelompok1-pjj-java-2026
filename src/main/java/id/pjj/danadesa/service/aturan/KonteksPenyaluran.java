package id.pjj.danadesa.service.aturan;

import id.pjj.danadesa.model.Desa;
import id.pjj.danadesa.model.Penyaluran;
import id.pjj.danadesa.model.Tahap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Semua data yang dibutuhkan aturan untuk menilai satu penyaluran baru. */
public record KonteksPenyaluran(Desa desa, List<Penyaluran> riwayat, Penyaluran baru, LocalDate hariIni) {

    public BigDecimal totalTersalur() {
        return riwayat.stream()
                .map(Penyaluran::getNilai)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal sisaPagu() {
        return desa.getPagu().subtract(totalTersalur());
    }

    public Optional<Penyaluran> cariTahap(Tahap tahap) {
        return riwayat.stream().filter(p -> p.getTahap() == tahap).findFirst();
    }
}

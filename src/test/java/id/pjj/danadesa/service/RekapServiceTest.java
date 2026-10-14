package id.pjj.danadesa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.pjj.danadesa.model.Desa;
import id.pjj.danadesa.model.Kabupaten;
import id.pjj.danadesa.model.Penyaluran;
import id.pjj.danadesa.model.RekapDesa;
import id.pjj.danadesa.model.RekapKabupaten;
import id.pjj.danadesa.model.Tahap;
import id.pjj.danadesa.service.RekapService.HasilRekap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class RekapServiceTest {

    private final Kabupaten sukamaju = new Kabupaten(1, "9901", "Kabupaten Sukamaju");
    private final Kabupaten tirtajaya = new Kabupaten(2, "9902", "Kabupaten Tirtajaya");

    private final Desa a = new Desa(1, "9901012001", "A", sukamaju, bd("1000"));
    private final Desa b = new Desa(2, "9901012002", "B", sukamaju, bd("3000"));
    private final Desa c = new Desa(3, "9902012001", "C", tirtajaya, bd("500"));

    private static BigDecimal bd(String s) {
        return new BigDecimal(s);
    }

    private static Penyaluran p(int desaId, Tahap t, String nilai) {
        return new Penyaluran(null, desaId, t, LocalDate.of(2026, 1, 1), bd(nilai));
    }

    private final List<Penyaluran> semua = List.of(
            p(1, Tahap.I, "400"), p(1, Tahap.II, "400"),
            p(2, Tahap.I, "1200"));

    @Test
    void rekapPerDesa() {
        List<RekapDesa> r = RekapService.hitungPerDesa(List.of(c, b, a), semua);

        assertEquals(List.of("9901012001", "9901012002", "9902012001"),
                r.stream().map(x -> x.getDesa().getKode()).toList(), "urut berdasarkan kode");
        RekapDesa ra = r.get(0);
        assertEquals(0, bd("800").compareTo(ra.getTersalur()));
        assertEquals(0, bd("400").compareTo(ra.getNilaiTahap(Tahap.II)));
        assertEquals(0, BigDecimal.ZERO.compareTo(ra.getNilaiTahap(Tahap.III)));
        assertEquals(bd("80.00"), ra.getPersentase());
    }

    @Test
    void desaTanpaPenyaluranNolPersen() {
        RekapDesa rc = RekapService.hitungPerDesa(List.of(c), semua).get(0);
        assertEquals(0, BigDecimal.ZERO.compareTo(rc.getTersalur()));
        assertEquals(bd("0.00"), rc.getPersentase());
        assertEquals(0, bd("500").compareTo(rc.getSisa()));
    }

    @Test
    void rekapPerKabupatenDanTotal() {
        HasilRekap h = RekapService.hitung(List.of(a, b, c), semua);

        List<RekapKabupaten> kab = h.perKabupaten();
        assertEquals(2, kab.size());
        RekapKabupaten s = kab.get(0);
        assertEquals("Kabupaten Sukamaju", s.getLabel());
        assertEquals(2, s.getJumlahDesa());
        assertEquals(0, bd("4000").compareTo(s.getPagu()));
        assertEquals(0, bd("2000").compareTo(s.getTersalur()));
        assertEquals(bd("50.00"), s.getPersentase());

        assertEquals(3, h.total().getJumlahDesa());
        assertEquals(0, bd("4500").compareTo(h.total().getPagu()));
        assertEquals(bd("44.44"), h.total().getPersentase());
    }

    @Test
    void sebaranTahapDesa() {
        var perDesa = RekapService.hitungPerDesa(List.of(a, b, c), semua);
        // a: 2 tahap, b: 1 tahap, c: belum
        assertEquals(java.util.Map.of(0, 1L, 1, 1L, 2, 1L, 3, 0L), RekapService.sebaranTahap(perDesa));
    }

    @Test
    void rekapPerBulan() {
        var data = List.of(
                new Penyaluran(null, 1, Tahap.I, LocalDate.of(2026, 2, 3), bd("100")),
                new Penyaluran(null, 2, Tahap.I, LocalDate.of(2026, 2, 20), bd("50")),
                new Penyaluran(null, 1, Tahap.II, LocalDate.of(2026, 5, 1), bd("70")));
        var perBulan = RekapService.hitungPerBulan(data);
        assertEquals(List.of(java.time.YearMonth.of(2026, 2), java.time.YearMonth.of(2026, 5)),
                List.copyOf(perBulan.keySet()), "urut per bulan");
        assertEquals(0, bd("150").compareTo(perBulan.get(java.time.YearMonth.of(2026, 2)).get(Tahap.I)));
        assertEquals(0, bd("70").compareTo(perBulan.get(java.time.YearMonth.of(2026, 5)).get(Tahap.II)));
    }
}

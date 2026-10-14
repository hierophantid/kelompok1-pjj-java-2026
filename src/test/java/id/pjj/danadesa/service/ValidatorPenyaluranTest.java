package id.pjj.danadesa.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.pjj.danadesa.exception.AturanBisnisException;
import id.pjj.danadesa.exception.MelebihiSisaPaguException;
import id.pjj.danadesa.model.Desa;
import id.pjj.danadesa.model.Kabupaten;
import id.pjj.danadesa.model.Penyaluran;
import id.pjj.danadesa.model.Tahap;
import id.pjj.danadesa.service.aturan.KonteksPenyaluran;
import id.pjj.danadesa.service.aturan.ValidatorPenyaluran;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidatorPenyaluranTest {

    private static final LocalDate HARI_INI = LocalDate.of(2026, 10, 14);
    private final ValidatorPenyaluran validator = ValidatorPenyaluran.standar();
    private final Desa desa = new Desa(1, "9901012001", "Mekarsari",
            new Kabupaten(1, "9901", "Kabupaten Sukamaju"), rp(1_000_000_000));

    private static BigDecimal rp(long n) {
        return BigDecimal.valueOf(n);
    }

    private static Penyaluran salur(Tahap t, String tgl, long nilai) {
        return new Penyaluran(null, 1, t, LocalDate.parse(tgl), rp(nilai));
    }

    private void periksa(List<Penyaluran> riwayat, Penyaluran baru) throws AturanBisnisException {
        validator.periksa(new KonteksPenyaluran(desa, riwayat, baru, HARI_INI));
    }

    @Test
    void tahapPertamaDalamPaguDiterima() {
        assertDoesNotThrow(() -> periksa(List.of(), salur(Tahap.I, "2026-02-10", 400_000_000)));
    }

    @Test
    void nilaiTepatSamaDenganSisaDiterima() {
        var riwayat = List.of(salur(Tahap.I, "2026-02-10", 400_000_000),
                salur(Tahap.II, "2026-05-10", 400_000_000));
        assertDoesNotThrow(() -> periksa(riwayat, salur(Tahap.III, "2026-08-10", 200_000_000)));
    }

    @Test
    void melebihiSisaPaguDitolakDenganCustomException() {
        var riwayat = List.of(salur(Tahap.I, "2026-02-10", 700_000_000));
        MelebihiSisaPaguException e = assertThrows(MelebihiSisaPaguException.class,
                () -> periksa(riwayat, salur(Tahap.II, "2026-05-10", 300_000_001)));
        assertEquals(0, rp(300_000_000).compareTo(e.getSisaPagu()));
    }

    @Test
    void tahapDuaTanpaTahapSatuDitolak() {
        var e = assertThrows(AturanBisnisException.class,
                () -> periksa(List.of(), salur(Tahap.II, "2026-05-10", 100)));
        assertTrue(e.getMessage().contains("Tahap I belum disalurkan"));
    }

    @Test
    void tahapGandaDitolak() {
        var riwayat = List.of(salur(Tahap.I, "2026-02-10", 100));
        var e = assertThrows(AturanBisnisException.class,
                () -> periksa(riwayat, salur(Tahap.I, "2026-03-10", 100)));
        assertTrue(e.getMessage().contains("sudah disalurkan"));
    }

    @Test
    void tanggalSebelumTahapSebelumnyaDitolak() {
        var riwayat = List.of(salur(Tahap.I, "2026-04-10", 100));
        var e = assertThrows(AturanBisnisException.class,
                () -> periksa(riwayat, salur(Tahap.II, "2026-04-09", 100)));
        assertTrue(e.getMessage().contains("tidak boleh sebelum"));
    }

    @Test
    void tanggalMasaDepanDitolak() {
        var e = assertThrows(AturanBisnisException.class,
                () -> periksa(List.of(), salur(Tahap.I, "2026-10-15", 100)));
        assertTrue(e.getMessage().contains("melewati hari ini"));
    }
}

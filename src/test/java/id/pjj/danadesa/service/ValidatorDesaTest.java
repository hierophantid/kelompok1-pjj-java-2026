package id.pjj.danadesa.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import id.pjj.danadesa.exception.AturanBisnisException;
import id.pjj.danadesa.exception.ValidasiException;
import id.pjj.danadesa.model.Desa;
import id.pjj.danadesa.model.Kabupaten;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ValidatorDesaTest {

    private final Kabupaten kab = new Kabupaten(1, "9901", "Kabupaten Sukamaju");

    private Desa desa(String kode, String nama, Kabupaten k, long pagu) {
        return new Desa(null, kode, nama, k, BigDecimal.valueOf(pagu));
    }

    @Test
    void desaValid() {
        assertDoesNotThrow(() -> ValidatorDesa.validasi(desa("9901012001", "Mekarsari", kab, 1_000)));
    }

    @Test
    void kodeHarus10Digit() {
        assertThrows(ValidasiException.class, () -> ValidatorDesa.validasi(desa("99010120", "X", kab, 1)));
        assertThrows(ValidasiException.class, () -> ValidatorDesa.validasi(desa("99010120AB", "X", kab, 1)));
    }

    @Test
    void kabupatenWajib() {
        assertThrows(ValidasiException.class, () -> ValidatorDesa.validasi(desa("9901012001", "X", null, 1)));
    }

    @Test
    void kodeDesaHarusDiawaliKodeKabupaten() {
        assertThrows(AturanBisnisException.class,
                () -> ValidatorDesa.validasi(desa("9902012001", "X", kab, 1)));
    }
}

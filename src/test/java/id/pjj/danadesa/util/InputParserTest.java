package id.pjj.danadesa.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.pjj.danadesa.exception.ValidasiException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InputParserTest {

    @Test
    void nilaiPolos() throws Exception {
        assertEquals(new BigDecimal("250000000"), InputParser.rupiah("250000000", "Nilai"));
    }

    @Test
    void nilaiDenganPemisahRibuanDanRp() throws Exception {
        assertEquals(0, new BigDecimal("1250000000").compareTo(InputParser.rupiah("Rp 1.250.000.000", "Nilai")));
        assertEquals(0, new BigDecimal("1500.5").compareTo(InputParser.rupiah("1.500,50", "Nilai")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void nilaiKosongDitolak(String teks) {
        ValidasiException e = assertThrows(ValidasiException.class, () -> InputParser.rupiah(teks, "Nilai"));
        assertTrue(e.getMessage().contains("wajib diisi"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "12a", "1.2.3", "1,000,000", "12.34"})
    void nilaiBukanAngkaDitolak(String teks) {
        ValidasiException e = assertThrows(ValidasiException.class, () -> InputParser.rupiah(teks, "Nilai"));
        assertTrue(e.getMessage().contains("harus berupa angka"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-500", "0,00"})
    void nilaiNolAtauNegatifDitolak(String teks) {
        ValidasiException e = assertThrows(ValidasiException.class, () -> InputParser.rupiah(teks, "Nilai"));
        assertTrue(e.getMessage().contains("lebih dari 0"));
    }

    @Test
    void tanggalValid() throws Exception {
        assertEquals(LocalDate.of(2026, 3, 15), InputParser.tanggal("15-03-2026", "Tanggal"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-03-15", "31-02-2026", "15/03/2026", "besok"})
    void tanggalTidakValidDitolak(String teks) {
        assertThrows(ValidasiException.class, () -> InputParser.tanggal(teks, "Tanggal"));
    }
}

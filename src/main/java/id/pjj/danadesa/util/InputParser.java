package id.pjj.danadesa.util;

import id.pjj.danadesa.exception.ValidasiException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/** Mengubah teks dari form menjadi tipe data, dengan pesan error yang jelas. */
public final class InputParser {

    /** Batas DECIMAL(18,2) di database. */
    public static final BigDecimal NILAI_MAKSIMUM = new BigDecimal("9999999999999999.99");

    // 250000000 | 250.000.000 | 250.000.000,50 | 250000000,5
    private static final Pattern POLA_ANGKA = Pattern.compile(
            "\\d{1,3}(\\.\\d{3})+(,\\d{1,2})?|\\d+(,\\d{1,2})?");

    private InputParser() {
    }

    public static String wajib(String teks, String namaField) throws ValidasiException {
        if (teks == null || teks.isBlank()) {
            throw new ValidasiException(namaField + " wajib diisi.");
        }
        return teks.trim();
    }

    public static BigDecimal rupiah(String teks, String namaField) throws ValidasiException {
        String bersih = wajib(teks, namaField)
                .replaceFirst("(?i)^rp\\.?", "")
                .replace(" ", "");
        if (bersih.startsWith("-")) {
            throw new ValidasiException(namaField + " harus lebih dari 0.");
        }
        if (!POLA_ANGKA.matcher(bersih).matches()) {
            throw new ValidasiException(namaField
                    + " harus berupa angka, contoh: 250000000 atau 250.000.000");
        }
        BigDecimal nilai = new BigDecimal(bersih.replace(".", "").replace(',', '.'));
        if (nilai.signum() <= 0) {
            throw new ValidasiException(namaField + " harus lebih dari 0.");
        }
        if (nilai.compareTo(NILAI_MAKSIMUM) > 0) {
            throw new ValidasiException(namaField + " terlalu besar.");
        }
        return nilai;
    }

    public static LocalDate tanggal(String teks, String namaField) throws ValidasiException {
        String bersih = wajib(teks, namaField);
        try {
            return LocalDate.parse(bersih, Format.TANGGAL);
        } catch (DateTimeParseException e) {
            throw new ValidasiException(namaField
                    + " tidak valid. Gunakan format dd-MM-yyyy, contoh: 15-03-2026");
        }
    }
}

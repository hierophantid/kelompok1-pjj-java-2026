package id.pjj.danadesa.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Format tampilan angka rupiah, persen, dan tanggal ala Indonesia. */
public final class Format {

    public static final Locale INDONESIA = Locale.forLanguageTag("id-ID");
    public static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    // DecimalFormat tidak thread-safe, jadi satu instance per thread
    private static final ThreadLocal<DecimalFormat> RIBUAN = ThreadLocal.withInitial(
            () -> new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(INDONESIA)));

    private Format() {
    }

    public static String rupiah(BigDecimal nilai) {
        if (nilai == null) {
            return "-";
        }
        String angka = RIBUAN.get().format(nilai.abs());
        return (nilai.signum() < 0 ? "-Rp " : "Rp ") + angka;
    }

    /** Angka dengan pemisah ribuan tanpa "Rp": 1.120.000.000 */
    public static String angka(BigDecimal nilai) {
        return nilai == null ? "-" : RIBUAN.get().format(nilai);
    }

    /** Rupiah singkat untuk kartu ringkasan: Rp 15,70 M / Rp 850,5 jt. */
    public static String rupiahRingkas(BigDecimal nilai) {
        if (nilai == null) {
            return "-";
        }
        double v = nilai.doubleValue();
        if (Math.abs(v) >= 1e12) {
            return String.format(INDONESIA, "Rp %.2f T", v / 1e12);
        }
        if (Math.abs(v) >= 1e9) {
            return String.format(INDONESIA, "Rp %.2f M", v / 1e9);
        }
        if (Math.abs(v) >= 1e6) {
            return String.format(INDONESIA, "Rp %.1f jt", v / 1e6);
        }
        return rupiah(nilai);
    }

    public static String persen(BigDecimal nilai) {
        return nilai == null ? "-" : String.format(INDONESIA, "%.2f%%", nilai);
    }

    public static String tanggal(LocalDate tanggal) {
        return tanggal == null ? "" : TANGGAL.format(tanggal);
    }

    /** Angka untuk CSV: tanpa pemisah ribuan, koma desimal (cocok untuk Excel Indonesia). */
    public static String angkaCsv(BigDecimal nilai) {
        return nilai.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
    }
}

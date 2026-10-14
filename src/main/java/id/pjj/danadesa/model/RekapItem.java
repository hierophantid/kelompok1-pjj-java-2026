package id.pjj.danadesa.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Induk abstrak untuk semua baris rekap. Subclass (RekapDesa, RekapKabupaten,
 * RekapTotal) menentukan label dan kolom CSV-nya sendiri (polimorfisme).
 */
public abstract class RekapItem implements BarisCsv {

    private static final BigDecimal SERATUS = BigDecimal.valueOf(100);

    private final BigDecimal pagu;
    private final BigDecimal tersalur;

    protected RekapItem(BigDecimal pagu, BigDecimal tersalur) {
        this.pagu = pagu;
        this.tersalur = tersalur;
    }

    public BigDecimal getPagu() {
        return pagu;
    }

    public BigDecimal getTersalur() {
        return tersalur;
    }

    public BigDecimal getSisa() {
        return pagu.subtract(tersalur);
    }

    /** Persentase penyaluran terhadap pagu, 2 angka desimal. */
    public BigDecimal getPersentase() {
        if (pagu.signum() == 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return tersalur.multiply(SERATUS).divide(pagu, 2, RoundingMode.HALF_UP);
    }

    public abstract String getLabel();
}

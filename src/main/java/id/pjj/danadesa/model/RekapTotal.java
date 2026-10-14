package id.pjj.danadesa.model;

import id.pjj.danadesa.util.Format;
import java.math.BigDecimal;

/** Baris total keseluruhan. */
public class RekapTotal extends RekapItem {

    private final int jumlahDesa;

    public RekapTotal(int jumlahDesa, BigDecimal pagu, BigDecimal tersalur) {
        super(pagu, tersalur);
        this.jumlahDesa = jumlahDesa;
    }

    public int getJumlahDesa() {
        return jumlahDesa;
    }

    @Override
    public String getLabel() {
        return "TOTAL (" + jumlahDesa + " desa)";
    }

    @Override
    public String[] kolomCsv() {
        return new String[]{
            "", "TOTAL", String.valueOf(jumlahDesa),
            Format.angkaCsv(getPagu()), Format.angkaCsv(getTersalur()),
            Format.angkaCsv(getSisa()), Format.angkaCsv(getPersentase())
        };
    }
}

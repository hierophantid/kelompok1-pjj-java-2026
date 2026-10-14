package id.pjj.danadesa.model;

import id.pjj.danadesa.util.Format;
import java.math.BigDecimal;

public class RekapKabupaten extends RekapItem {

    public static final String[] HEADER_CSV = {
        "Kode Kabupaten", "Nama Kabupaten", "Jumlah Desa", "Pagu", "Total Tersalur", "Sisa Pagu", "Persentase (%)"
    };

    private final Kabupaten kabupaten;
    private final int jumlahDesa;

    public RekapKabupaten(Kabupaten kabupaten, int jumlahDesa, BigDecimal pagu, BigDecimal tersalur) {
        super(pagu, tersalur);
        this.kabupaten = kabupaten;
        this.jumlahDesa = jumlahDesa;
    }

    public Kabupaten getKabupaten() {
        return kabupaten;
    }

    public int getJumlahDesa() {
        return jumlahDesa;
    }

    @Override
    public String getLabel() {
        return kabupaten.getNama();
    }

    @Override
    public String[] kolomCsv() {
        return new String[]{
            kabupaten.getKode(), kabupaten.getNama(), String.valueOf(jumlahDesa),
            Format.angkaCsv(getPagu()), Format.angkaCsv(getTersalur()),
            Format.angkaCsv(getSisa()), Format.angkaCsv(getPersentase())
        };
    }
}

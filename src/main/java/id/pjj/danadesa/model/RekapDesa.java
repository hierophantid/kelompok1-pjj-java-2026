package id.pjj.danadesa.model;

import id.pjj.danadesa.util.Format;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public class RekapDesa extends RekapItem {

    public static final String[] HEADER_CSV = {
        "Kode Desa", "Nama Desa", "Kabupaten", "Pagu",
        "Tahap I", "Tahap II", "Tahap III", "Total Tersalur", "Sisa Pagu", "Persentase (%)"
    };

    private final Desa desa;
    private final Map<Tahap, BigDecimal> nilaiPerTahap;

    public RekapDesa(Desa desa, Map<Tahap, BigDecimal> nilaiPerTahap) {
        super(desa.getPagu(), jumlahkan(nilaiPerTahap));
        this.desa = desa;
        this.nilaiPerTahap = new EnumMap<>(Tahap.class);
        this.nilaiPerTahap.putAll(nilaiPerTahap);
    }

    private static BigDecimal jumlahkan(Map<Tahap, BigDecimal> nilai) {
        return nilai.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Desa getDesa() {
        return desa;
    }

    public BigDecimal getNilaiTahap(Tahap tahap) {
        return nilaiPerTahap.getOrDefault(tahap, BigDecimal.ZERO);
    }

    /** Jumlah tahap yang sudah disalurkan (0..3). */
    public int getJumlahTahap() {
        return (int) nilaiPerTahap.values().stream().filter(v -> v.signum() > 0).count();
    }

    @Override
    public String getLabel() {
        return desa.getKode() + " - " + desa.getNama();
    }

    @Override
    public String[] kolomCsv() {
        return new String[]{
            desa.getKode(), desa.getNama(), desa.getKabupaten().getNama(),
            Format.angkaCsv(getPagu()),
            Format.angkaCsv(getNilaiTahap(Tahap.I)),
            Format.angkaCsv(getNilaiTahap(Tahap.II)),
            Format.angkaCsv(getNilaiTahap(Tahap.III)),
            Format.angkaCsv(getTersalur()),
            Format.angkaCsv(getSisa()),
            Format.angkaCsv(getPersentase())
        };
    }
}

package id.pjj.danadesa.service;

import id.pjj.danadesa.dao.DesaDao;
import id.pjj.danadesa.dao.PenyaluranDao;
import id.pjj.danadesa.model.Desa;
import id.pjj.danadesa.model.Kabupaten;
import id.pjj.danadesa.model.Penyaluran;
import id.pjj.danadesa.model.RekapDesa;
import id.pjj.danadesa.model.RekapKabupaten;
import id.pjj.danadesa.model.RekapItem;
import id.pjj.danadesa.model.RekapTotal;
import id.pjj.danadesa.model.Tahap;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Rekap penyaluran per desa, per kabupaten, dan total (Collections + Stream API). */
public class RekapService {

    public record HasilRekap(List<RekapDesa> perDesa, List<RekapKabupaten> perKabupaten, RekapTotal total,
            Map<YearMonth, Map<Tahap, BigDecimal>> perBulan) {
    }

    private final DesaDao desaDao;
    private final PenyaluranDao penyaluranDao;

    public RekapService(DesaDao desaDao, PenyaluranDao penyaluranDao) {
        this.desaDao = desaDao;
        this.penyaluranDao = penyaluranDao;
    }

    public HasilRekap muatRekap() {
        return hitung(desaDao.findAll(), penyaluranDao.findAll());
    }

    // ---- method statis murni (tanpa database) supaya mudah diuji dengan JUnit ----
    public static HasilRekap hitung(List<Desa> daftarDesa, List<Penyaluran> semuaPenyaluran) {
        List<RekapDesa> perDesa = hitungPerDesa(daftarDesa, semuaPenyaluran);
        return new HasilRekap(perDesa, hitungPerKabupaten(perDesa), hitungTotal(perDesa),
                hitungPerBulan(semuaPenyaluran));
    }

    public static List<RekapDesa> hitungPerDesa(List<Desa> daftarDesa, List<Penyaluran> semua) {
        // desaId -> (tahap -> jumlah nilai)
        Map<Integer, Map<Tahap, BigDecimal>> nilaiPerDesa = semua.stream()
                .collect(Collectors.groupingBy(Penyaluran::getDesaId,
                        Collectors.groupingBy(Penyaluran::getTahap,
                                () -> new EnumMap<>(Tahap.class),
                                Collectors.reducing(BigDecimal.ZERO, Penyaluran::getNilai, BigDecimal::add))));

        return daftarDesa.stream()
                .map(d -> new RekapDesa(d, nilaiPerDesa.getOrDefault(d.getId(), Map.of())))
                .sorted(Comparator.comparing(r -> r.getDesa().getKode()))
                .toList();
    }

    public static List<RekapKabupaten> hitungPerKabupaten(List<RekapDesa> perDesa) {
        Map<Kabupaten, List<RekapDesa>> kelompok = perDesa.stream()
                .collect(Collectors.groupingBy(r -> r.getDesa().getKabupaten(),
                        () -> new TreeMap<>(Comparator.comparing(Kabupaten::getKode)),
                        Collectors.toList()));

        return kelompok.entrySet().stream()
                .map(e -> new RekapKabupaten(e.getKey(), e.getValue().size(),
                        jumlah(e.getValue(), RekapItem::getPagu),
                        jumlah(e.getValue(), RekapItem::getTersalur)))
                .toList();
    }

    public static RekapTotal hitungTotal(List<RekapDesa> perDesa) {
        return new RekapTotal(perDesa.size(),
                jumlah(perDesa, RekapItem::getPagu),
                jumlah(perDesa, RekapItem::getTersalur));
    }

    /** Nilai penyaluran per bulan, dipecah per tahap (TreeMap: urut bulan). */
    public static Map<YearMonth, Map<Tahap, BigDecimal>> hitungPerBulan(List<Penyaluran> semua) {
        return semua.stream().collect(Collectors.groupingBy(
                p -> YearMonth.from(p.getTanggal()),
                TreeMap::new,
                Collectors.groupingBy(Penyaluran::getTahap,
                        () -> new EnumMap<>(Tahap.class),
                        Collectors.reducing(BigDecimal.ZERO, Penyaluran::getNilai, BigDecimal::add))));
    }

    /** Banyaknya desa per jumlah tahap tersalur: 0 (belum), 1, 2, 3 (selesai). */
    public static Map<Integer, Long> sebaranTahap(List<RekapDesa> perDesa) {
        Map<Integer, Long> hasil = new TreeMap<>();
        for (int i = 0; i <= Tahap.values().length; i++) {
            hasil.put(i, 0L);
        }
        hasil.putAll(perDesa.stream()
                .collect(Collectors.groupingBy(RekapDesa::getJumlahTahap, Collectors.counting())));
        return hasil;
    }

    private static BigDecimal jumlah(List<? extends RekapItem> data,
            java.util.function.Function<RekapItem, BigDecimal> ambil) {
        return data.stream().map(ambil).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

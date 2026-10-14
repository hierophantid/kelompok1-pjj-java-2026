package id.pjj.danadesa.ui;

import id.pjj.danadesa.model.RekapTotal;
import id.pjj.danadesa.model.Tahap;
import id.pjj.danadesa.service.RekapService;
import id.pjj.danadesa.service.RekapService.HasilRekap;
import id.pjj.danadesa.ui.komponen.GrafikBatang;
import id.pjj.danadesa.ui.komponen.GrafikDonat;
import id.pjj.danadesa.ui.komponen.GrafikKolom;
import id.pjj.danadesa.ui.komponen.Kartu;
import id.pjj.danadesa.ui.komponen.Pesan;
import id.pjj.danadesa.ui.komponen.StatKartu;
import id.pjj.danadesa.ui.komponen.Tema;
import id.pjj.danadesa.util.Format;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/** Ringkasan: kartu angka utama dan tiga grafik. */
public class DashboardPanel extends JPanel implements Halaman {

    private static final String[] BULAN = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
        "Jul", "Agu", "Sep", "Okt", "Nov", "Des"};

    private final RekapService rekapService;

    private final StatKartu kartuDesa = new StatKartu("Jumlah Desa", Tema.UTAMA);
    private final StatKartu kartuPagu = new StatKartu("Total Pagu", Tema.UTAMA);
    private final StatKartu kartuSalur = new StatKartu("Total Tersalur", Tema.UTAMA);
    private final StatKartu kartuPersen = new StatKartu("Realisasi Penyaluran", Tema.UTAMA);

    private final GrafikKolom grafikBulan = new GrafikKolom();
    private final GrafikBatang grafikKabupaten = new GrafikBatang();
    private final GrafikDonat grafikStatus = new GrafikDonat();
    private final javax.swing.JLabel lblSubBulan = Tema.redup(" ");

    public DashboardPanel(RekapService rekapService) {
        super(new GridBagLayout());
        this.rekapService = rekapService;
        setOpaque(false);

        JPanel barisStat = Tema.panel(new GridLayout(1, 4, 16, 0));
        barisStat.add(kartuDesa);
        barisStat.add(kartuPagu);
        barisStat.add(kartuSalur);
        barisStat.add(kartuPersen);

        Kartu kartuBulan = kartuGrafik("Penyaluran per Bulan", lblSubBulan, grafikBulan);
        Kartu kartuKab = kartuGrafik("Pagu vs Tersalur", Tema.redup("Per kabupaten"), grafikKabupaten);
        Kartu kartuStatus = kartuGrafik("Status Desa", Tema.redup("Tahap terakhir yang disalurkan"), grafikStatus);

        JPanel barisGrafik = Tema.panel(new GridBagLayout());
        tambah(barisGrafik, kartuBulan, 0, 0.50, new Insets(0, 0, 0, 16));
        tambah(barisGrafik, kartuKab, 1, 0.27, new Insets(0, 0, 0, 16));
        tambah(barisGrafik, kartuStatus, 2, 0.23, new Insets(0, 0, 0, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.BOTH;
        g.gridy = 0;
        g.insets = new Insets(0, 0, 16, 0);
        add(barisStat, g);
        g.gridy = 1;
        g.weighty = 1;
        g.insets = new Insets(0, 0, 0, 0);
        add(barisGrafik, g);
    }

    private static Kartu kartuGrafik(String judul, javax.swing.JLabel sub, JComponent grafik) {
        Kartu k = new Kartu(new BorderLayout(0, 14));
        JPanel kepala = Tema.panel(new GridLayout(2, 1, 0, 2));
        kepala.add(Tema.judul(judul, 15f));
        kepala.add(sub);
        k.add(kepala, BorderLayout.NORTH);
        k.add(grafik, BorderLayout.CENTER);
        return k;
    }

    private static void tambah(JPanel p, JComponent c, int x, double bobot, Insets insets) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = 0;
        g.weightx = bobot;
        g.weighty = 1;
        g.fill = GridBagConstraints.BOTH;
        g.insets = insets;
        p.add(c, g);
    }

    @Override
    public void muatUlang() {
        new SwingWorker<HasilRekap, Void>() {
            @Override
            protected HasilRekap doInBackground() {
                return rekapService.muatRekap();
            }

            @Override
            protected void done() {
                try {
                    tampilkan(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Pesan.error(DashboardPanel.this, e.getCause());
                }
            }
        }.execute();
    }

    private void tampilkan(HasilRekap h) {
        RekapTotal t = h.total();
        kartuDesa.setNilai(String.valueOf(t.getJumlahDesa()), "tersebar di " + h.perKabupaten().size() + " kabupaten");
        kartuPagu.setNilai(Format.rupiahRingkas(t.getPagu()), Format.rupiah(t.getPagu()));
        kartuSalur.setNilai(Format.rupiahRingkas(t.getTersalur()), "sisa " + Format.rupiah(t.getSisa()));
        kartuPersen.setNilai(Format.persen(t.getPersentase()), "dari total pagu Dana Desa");

        // 1. kolom per bulan (Januari s.d. Desember tahun data terakhir)
        int tahun = h.perBulan().keySet().stream().reduce((a, b) -> b)
                .map(YearMonth::getYear).orElse(LocalDate.now().getYear());
        List<String> label = new ArrayList<>();
        List<String> ket = new ArrayList<>();
        List<double[]> nilai = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(tahun, m);
            Map<Tahap, BigDecimal> v = h.perBulan().getOrDefault(ym, Map.of());
            label.add(BULAN[m - 1]);
            ket.add(Month.of(m).getDisplayName(TextStyle.FULL, Format.INDONESIA) + " " + tahun);
            nilai.add(new double[]{
                v.getOrDefault(Tahap.I, BigDecimal.ZERO).doubleValue(),
                v.getOrDefault(Tahap.II, BigDecimal.ZERO).doubleValue(),
                v.getOrDefault(Tahap.III, BigDecimal.ZERO).doubleValue()});
        }
        grafikBulan.setData(label, ket, nilai, new String[]{"Tahap I", "Tahap II", "Tahap III"});
        lblSubBulan.setText("Nilai tersalur tiap bulan tahun " + tahun + ", menurut tahap");

        // 2. pagu vs tersalur per kabupaten
        grafikKabupaten.setData(h.perKabupaten().stream()
                .map(k -> new GrafikBatang.Item(k.getLabel().replace("Kabupaten ", "Kab. "),
                        k.getPagu(), k.getTersalur(), k.getPersentase()))
                .toList());

        // 3. donat status desa (navy tua = paling maju)
        Map<Integer, Long> sebaran = RekapService.sebaranTahap(h.perDesa());
        grafikStatus.setData(List.of(
                new GrafikDonat.Irisan("Selesai (Tahap III)", sebaran.get(3), Tema.NAVY_700),
                new GrafikDonat.Irisan("Sampai Tahap II", sebaran.get(2), Tema.NAVY_500),
                new GrafikDonat.Irisan("Baru Tahap I", sebaran.get(1), Tema.NAVY_300),
                new GrafikDonat.Irisan("Belum disalurkan", sebaran.get(0), Tema.SILVER_200)), "desa");
    }
}

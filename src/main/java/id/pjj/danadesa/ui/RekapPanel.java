package id.pjj.danadesa.ui;

import static id.pjj.danadesa.ui.komponen.ListTableModel.kolom;

import id.pjj.danadesa.model.RekapDesa;
import id.pjj.danadesa.model.RekapItem;
import id.pjj.danadesa.model.RekapKabupaten;
import id.pjj.danadesa.model.RekapTotal;
import id.pjj.danadesa.model.Tahap;
import id.pjj.danadesa.service.CsvExporter;
import id.pjj.danadesa.service.RekapService;
import id.pjj.danadesa.service.RekapService.HasilRekap;
import id.pjj.danadesa.ui.komponen.Kartu;
import id.pjj.danadesa.ui.komponen.ListTableModel;
import id.pjj.danadesa.ui.komponen.Pesan;
import id.pjj.danadesa.ui.komponen.Tabel;
import id.pjj.danadesa.ui.komponen.Tema;
import id.pjj.danadesa.util.Format;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingWorker;

/**
 * Halaman "Rekap": rekap per desa dan per kabupaten.
 * Perhitungan rekap dan ekspor CSV berjalan di SwingWorker (thread terpisah)
 * sehingga tampilan tidak membeku.
 */
public class RekapPanel extends JPanel implements Halaman {

    private final RekapService rekapService;
    private final CsvExporter exporter;

    private final JButton btnMuat = Tema.tombol("Muat Ulang");
    private final JButton btnEkspor = Tema.tombolUtama("Ekspor CSV");
    private final JProgressBar progres = new JProgressBar();
    private final JLabel lblTotalPagu = Tema.judul("-", 15f);
    private final JLabel lblTotalSalur = Tema.judul("-", 15f);
    private final JLabel lblTotalSisa = Tema.judul("-", 15f);
    private final JLabel lblTotalPersen = Tema.judul("-", 15f);

    private final ListTableModel<RekapDesa> modelDesa = new ListTableModel<>(List.of(
            kolom("Kode", String.class, (RekapDesa r) -> r.getDesa().getKode()),
            kolom("Nama Desa", String.class, (RekapDesa r) -> r.getDesa().getNama()),
            kolom("Pagu (Rp)", Tabel.Angka.class, RekapDesa::getPagu),
            kolom("Tahap I", Tabel.Angka.class, (RekapDesa r) -> r.getNilaiTahap(Tahap.I)),
            kolom("Tahap II", Tabel.Angka.class, (RekapDesa r) -> r.getNilaiTahap(Tahap.II)),
            kolom("Tahap III", Tabel.Angka.class, (RekapDesa r) -> r.getNilaiTahap(Tahap.III)),
            kolom("Tersalur (Rp)", Tabel.Angka.class, RekapDesa::getTersalur),
            kolom("Sisa (Rp)", Tabel.Angka.class, RekapDesa::getSisa),
            kolom("Realisasi", Tabel.Persen.class, RekapDesa::getPersentase)));

    private final ListTableModel<RekapKabupaten> modelKab = new ListTableModel<>(List.of(
            kolom("Kode", String.class, (RekapKabupaten r) -> r.getKabupaten().getKode()),
            kolom("Kabupaten", String.class, RekapKabupaten::getLabel),
            kolom("Jumlah Desa", Integer.class, RekapKabupaten::getJumlahDesa),
            kolom("Pagu (Rp)", Tabel.Angka.class, RekapKabupaten::getPagu),
            kolom("Tersalur (Rp)", Tabel.Angka.class, RekapKabupaten::getTersalur),
            kolom("Sisa (Rp)", Tabel.Angka.class, RekapKabupaten::getSisa),
            kolom("Realisasi", Tabel.Persen.class, RekapKabupaten::getPersentase)));

    private HasilRekap hasilTerakhir;

    public RekapPanel(RekapService rekapService, CsvExporter exporter) {
        super(new GridBagLayout());
        this.rekapService = rekapService;
        this.exporter = exporter;
        setOpaque(false);

        progres.setIndeterminate(true);
        progres.setVisible(false);

        // ----- baris total + tombol -----
        Kartu total = new Kartu(new BorderLayout(16, 0));
        JPanel angka = Tema.panel(new GridLayout(2, 4, 24, 2));
        angka.add(Tema.redup("Total Pagu"));
        angka.add(Tema.redup("Total Tersalur"));
        angka.add(Tema.redup("Sisa Pagu"));
        angka.add(Tema.redup("Realisasi"));
        angka.add(lblTotalPagu);
        angka.add(lblTotalSalur);
        angka.add(lblTotalSisa);
        angka.add(lblTotalPersen);
        JPanel tombol = Tema.panel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        tombol.add(progres);
        tombol.add(btnMuat);
        tombol.add(btnEkspor);
        total.add(angka, BorderLayout.CENTER);
        total.add(tombol, BorderLayout.EAST);

        // ----- tabel -----
        JTable tabelDesa = Tabel.buat(modelDesa);
        Tabel.lebarKolom(tabelDesa, 100, 120, 110, 105, 105, 105, 110, 105, 135);
        Kartu kartuDesa = kartuTabel("Rekap per Desa", "Klik judul kolom untuk mengurutkan", Tabel.gulir(tabelDesa));
        Kartu kartuKab = kartuTabel("Rekap per Kabupaten", null, Tabel.gulir(Tabel.buat(modelKab)));

        tambah(total, 0, 0, new Insets(0, 0, 16, 0));
        tambah(kartuDesa, 1, 0.62, new Insets(0, 0, 16, 0));
        tambah(kartuKab, 2, 0.38, new Insets(0, 0, 0, 0));

        btnMuat.addActionListener(e -> muatUlang());
        btnEkspor.addActionListener(e -> ekspor());
    }

    private static Kartu kartuTabel(String judul, String sub, JComponent isi) {
        Kartu k = new Kartu(new BorderLayout(0, 12));
        JPanel kepala = Tema.panel(new BorderLayout());
        kepala.add(Tema.judul(judul, 16f), BorderLayout.WEST);
        if (sub != null) {
            kepala.add(Tema.redup(sub), BorderLayout.EAST);
        }
        k.add(kepala, BorderLayout.NORTH);
        k.add(isi, BorderLayout.CENTER);
        return k;
    }

    private void tambah(JComponent c, int y, double bobot, Insets insets) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = y;
        g.weightx = 1;
        g.weighty = bobot;
        g.fill = GridBagConstraints.BOTH;
        g.insets = insets;
        add(c, g);
    }

    @Override
    public void muatUlang() {
        sibuk(true);
        new SwingWorker<HasilRekap, Void>() {
            @Override
            protected HasilRekap doInBackground() {
                return rekapService.muatRekap();          // jalan di thread latar
            }

            @Override
            protected void done() {                        // kembali di EDT
                sibuk(false);
                try {
                    tampilkan(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Pesan.error(RekapPanel.this, e.getCause());
                }
            }
        }.execute();
    }

    private void tampilkan(HasilRekap h) {
        hasilTerakhir = h;
        modelDesa.setData(h.perDesa());
        modelKab.setData(h.perKabupaten());
        RekapTotal t = h.total();
        lblTotalPagu.setText(Format.rupiah(t.getPagu()));
        lblTotalSalur.setText(Format.rupiah(t.getTersalur()));
        lblTotalSisa.setText(Format.rupiah(t.getSisa()));
        lblTotalPersen.setText(Format.persen(t.getPersentase()));
        lblTotalPersen.setForeground(Tema.UTAMA);
    }

    private void ekspor() {
        if (hasilTerakhir == null) {
            Pesan.info(this, "Muat rekap terlebih dahulu.");
            return;
        }
        JFileChooser pilih = new JFileChooser();
        pilih.setDialogTitle("Pilih folder tujuan ekspor");
        pilih.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (pilih.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path folder = pilih.getSelectedFile().toPath();
        HasilRekap data = hasilTerakhir;
        String stempel = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        sibuk(true);
        new SwingWorker<List<Path>, Void>() {
            @Override
            protected List<Path> doInBackground() throws Exception {
                Path fileDesa = folder.resolve("rekap_desa_" + stempel + ".csv");
                Path fileKab = folder.resolve("rekap_kabupaten_" + stempel + ".csv");
                exporter.tulis(fileDesa, RekapDesa.HEADER_CSV, data.perDesa());

                // RekapKabupaten dan RekapTotal sama-sama RekapItem -> satu list, kolom CSV polimorfik
                List<RekapItem> barisKab = new ArrayList<>(data.perKabupaten());
                barisKab.add(data.total());
                exporter.tulis(fileKab, RekapKabupaten.HEADER_CSV, barisKab);
                return List.of(fileDesa, fileKab);
            }

            @Override
            protected void done() {
                sibuk(false);
                try {
                    List<Path> hasil = get();
                    Pesan.info(RekapPanel.this, "Ekspor selesai:\n"
                            + hasil.stream().map(Path::toString).reduce((a, b) -> a + "\n" + b).orElse(""));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Pesan.error(RekapPanel.this, e.getCause());
                }
            }
        }.execute();
    }

    private void sibuk(boolean ya) {
        progres.setVisible(ya);
        btnMuat.setEnabled(!ya);
        btnEkspor.setEnabled(!ya);
        revalidate();
    }
}

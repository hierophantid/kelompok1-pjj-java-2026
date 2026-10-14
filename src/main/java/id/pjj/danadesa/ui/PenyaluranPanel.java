package id.pjj.danadesa.ui;

import static id.pjj.danadesa.ui.komponen.ListTableModel.kolom;

import id.pjj.danadesa.exception.AplikasiException;
import id.pjj.danadesa.exception.ValidasiException;
import id.pjj.danadesa.model.Desa;
import id.pjj.danadesa.model.Penyaluran;
import id.pjj.danadesa.model.RekapDesa;
import id.pjj.danadesa.model.Tahap;
import id.pjj.danadesa.service.DesaService;
import id.pjj.danadesa.service.PenyaluranService;
import id.pjj.danadesa.ui.komponen.BilahPersen;
import id.pjj.danadesa.ui.komponen.Form;
import id.pjj.danadesa.ui.komponen.Kartu;
import id.pjj.danadesa.ui.komponen.ListTableModel;
import id.pjj.danadesa.ui.komponen.Pesan;
import id.pjj.danadesa.ui.komponen.Tabel;
import id.pjj.danadesa.ui.komponen.Tema;
import id.pjj.danadesa.util.Format;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

/** Halaman "Penyaluran": catat penyaluran per tahap untuk desa terpilih. */
public class PenyaluranPanel extends JPanel implements Halaman {

    private final DesaService desaService;
    private final PenyaluranService service;

    private final JComboBox<Desa> cmbDesa = new JComboBox<>();
    private final JComboBox<Tahap> cmbTahap = new JComboBox<>(Tahap.values());
    private final JTextField txtTanggal = Tema.isian("dd-MM-yyyy");
    private final JTextField txtNilai = Tema.isian("contoh: 400.000.000");
    private final JButton btnCatat = Tema.tombolUtama("Catat Penyaluran");
    private final JButton btnHapus = Tema.tombolHapus("Hapus Tahap Terpilih");

    private final JLabel lblPagu = nilai();
    private final JLabel lblTersalur = nilai();
    private final JLabel lblSisa = nilai();
    private final BilahPersen bilah = new BilahPersen();
    private final JLabel lblDesaRiwayat = Tema.redup(" ");

    private final ListTableModel<Penyaluran> model = new ListTableModel<>(List.of(
            kolom("Tahap", Tahap.class, Penyaluran::getTahap),
            kolom("Tanggal", LocalDate.class, Penyaluran::getTanggal),
            kolom("Nilai", BigDecimal.class, Penyaluran::getNilai)));
    private final JTable tabel = Tabel.buat(model);

    public PenyaluranPanel(DesaService desaService, PenyaluranService service) {
        super(new BorderLayout(16, 0));
        this.desaService = desaService;
        this.service = service;
        setOpaque(false);

        // ----- ringkasan pagu desa terpilih -----
        Kartu ringkasan = new Kartu(new GridBagLayout());
        ringkasan.setWarna(Tema.SILVER_50, Tema.GARIS);
        ringkasan.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 14, 12, 14));
        baris(ringkasan, 0, "Pagu", lblPagu);
        baris(ringkasan, 1, "Sudah tersalur", lblTersalur);
        baris(ringkasan, 2, "Sisa pagu", lblSisa);
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 3;
        g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.insets = new Insets(8, 0, 0, 0);
        ringkasan.add(bilah, g);

        Form form = new Form()
                .judul("Catat Penyaluran", "Pilih desa, lalu isi tahap, tanggal, dan nilai")
                .tambah("Desa", cmbDesa)
                .tambahPenuh(ringkasan)
                .tambah("Tahap", cmbTahap)
                .tambah("Tanggal (dd-MM-yyyy)", txtTanggal)
                .tambah("Nilai Penyaluran (Rp)", txtNilai)
                .tambahPenuh(barisTombol(btnCatat))
                .selesai();
        Kartu kiri = new Kartu(new BorderLayout());
        kiri.setPreferredSize(new Dimension(360, 0));
        kiri.add(form);

        // ----- riwayat kanan -----
        JPanel kepala = Tema.panel(new GridLayout(2, 1, 0, 2));
        kepala.add(Tema.judul("Riwayat Penyaluran", 16f));
        kepala.add(lblDesaRiwayat);
        JPanel bawah = Tema.panel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bawah.add(btnHapus);

        Kartu kanan = new Kartu(new BorderLayout(0, 14));
        kanan.add(kepala, BorderLayout.NORTH);
        kanan.add(Tabel.gulir(tabel), BorderLayout.CENTER);
        kanan.add(bawah, BorderLayout.SOUTH);

        add(kiri, BorderLayout.WEST);
        add(kanan, BorderLayout.CENTER);

        // Hapus baru aktif setelah ada baris yang dipilih
        btnHapus.setEnabled(false);
        tabel.getSelectionModel().addListSelectionListener(e -> btnHapus.setEnabled(tabel.getSelectedRow() >= 0));
        cmbDesa.addActionListener(e -> tampilkanDesa());
        btnCatat.addActionListener(e -> jalankan(this::catat));
        btnHapus.addActionListener(e -> jalankan(this::hapus));
    }

    private static JPanel barisTombol(JButton b) {
        JPanel p = Tema.panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.add(b);
        return p;
    }

    private static JLabel nilai() {
        JLabel l = Tema.judul("-", 14f);
        l.setHorizontalAlignment(JLabel.RIGHT);
        return l;
    }

    private static void baris(JPanel p, int y, String label, JLabel nilai) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = y;
        g.insets = new Insets(y == 0 ? 0 : 6, 0, 0, 0);
        g.anchor = GridBagConstraints.WEST;
        g.weightx = 1;
        p.add(Tema.redup(label), g);
        g.anchor = GridBagConstraints.EAST;
        g.weightx = 0;
        p.add(nilai, g);
    }

    @Override
    public void muatUlang() {
        try {
            Desa sebelumnya = (Desa) cmbDesa.getSelectedItem();
            cmbDesa.setModel(new DefaultComboBoxModel<>(desaService.semuaDesa().toArray(Desa[]::new)));
            if (sebelumnya != null) {
                cmbDesa.setSelectedItem(sebelumnya);
            }
            tampilkanDesa();
        } catch (RuntimeException e) {
            Pesan.error(this, e);
        }
    }

    private void catat() throws AplikasiException {
        Desa desa = (Desa) cmbDesa.getSelectedItem();
        Penyaluran p = service.catat(desa, (Tahap) cmbTahap.getSelectedItem(),
                txtTanggal.getText(), txtNilai.getText());
        Pesan.info(this, p.getTahap().getLabel() + " untuk desa " + desa.getNama()
                + " sebesar " + Format.rupiah(p.getNilai()) + " berhasil dicatat.");
        txtNilai.setText("");
        tampilkanDesa();
    }

    private void hapus() throws AplikasiException {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            throw new ValidasiException("Pilih baris penyaluran yang akan dihapus.");
        }
        Penyaluran p = model.getBaris(tabel.convertRowIndexToModel(baris));
        if (!Pesan.konfirmasi(this, "Hapus " + p.getTahap().getLabel() + " sebesar "
                + Format.rupiah(p.getNilai()) + "?")) {
            return;
        }
        service.hapus(p);
        tampilkanDesa();
    }

    /** Tampilkan pagu, total tersalur, sisa, dan riwayat desa yang dipilih. */
    private void tampilkanDesa() {
        Desa desa = (Desa) cmbDesa.getSelectedItem();
        if (desa == null) {
            model.setData(List.of());
            lblPagu.setText("-");
            lblTersalur.setText("-");
            lblSisa.setText("-");
            bilah.setPersen(BigDecimal.ZERO);
            lblDesaRiwayat.setText("Belum ada desa");
            return;
        }
        try {
            List<Penyaluran> riwayat = service.riwayat(desa.getId());
            model.setData(riwayat);
            Map<Tahap, BigDecimal> perTahap = riwayat.stream().collect(Collectors.toMap(
                    Penyaluran::getTahap, Penyaluran::getNilai, BigDecimal::add, () -> new EnumMap<>(Tahap.class)));
            RekapDesa r = new RekapDesa(desa, perTahap);
            lblPagu.setText(Format.rupiah(r.getPagu()));
            lblTersalur.setText(Format.rupiah(r.getTersalur()));
            lblSisa.setText(Format.rupiah(r.getSisa()));
            lblSisa.setForeground(Tema.UTAMA);
            bilah.setPersen(r.getPersentase());
            lblDesaRiwayat.setText(desa.getKode() + " · " + desa.getNama() + " · " + desa.getKabupaten().getNama());

            // Sarankan tahap berikutnya dan tanggal hari ini (tetap divalidasi saat disimpan)
            Tahap saran = riwayat.stream().map(Penyaluran::getTahap)
                    .max(Comparator.naturalOrder())
                    .flatMap(Tahap::berikutnya)
                    .orElse(riwayat.isEmpty() ? Tahap.I : Tahap.III);
            cmbTahap.setSelectedItem(saran);
            if (txtTanggal.getText().isBlank()) {
                txtTanggal.setText(Format.tanggal(service.hariIni()));
            }
        } catch (RuntimeException e) {
            Pesan.error(this, e);
        }
    }

    private interface Aksi {
        void jalankan() throws AplikasiException;
    }

    private void jalankan(Aksi aksi) {
        try {
            aksi.jalankan();
        } catch (AplikasiException | RuntimeException e) {
            Pesan.error(this, e);
        }
    }
}

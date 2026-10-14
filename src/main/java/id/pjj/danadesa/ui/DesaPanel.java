package id.pjj.danadesa.ui;

import static id.pjj.danadesa.ui.komponen.ListTableModel.kolom;

import id.pjj.danadesa.exception.AplikasiException;
import id.pjj.danadesa.exception.ValidasiException;
import id.pjj.danadesa.model.Desa;
import id.pjj.danadesa.model.Kabupaten;
import id.pjj.danadesa.service.DesaService;
import id.pjj.danadesa.ui.komponen.Form;
import id.pjj.danadesa.ui.komponen.Kartu;
import id.pjj.danadesa.ui.komponen.ListTableModel;
import id.pjj.danadesa.ui.komponen.Pesan;
import id.pjj.danadesa.ui.komponen.Tabel;
import id.pjj.danadesa.ui.komponen.Tema;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

/** Halaman "Data Desa": CRUD lengkap entitas Desa. */
public class DesaPanel extends JPanel implements Halaman {

    private static final String MODE_BARU = "baru";
    private static final String MODE_UBAH = "ubah";

    private final DesaService service;

    private final JTextField txtKode = Tema.isian("contoh: 9901012001");
    private final JTextField txtNama = Tema.isian("contoh: Mekarsari");
    private final JComboBox<Kabupaten> cmbKabupaten = new JComboBox<>();
    private final JTextField txtPagu = Tema.isian("contoh: 1.250.000.000");
    private final JTextField txtCari = Tema.kotakCari("Cari kode, nama, atau kabupaten...");

    private final JButton btnTambah = Tema.tombolUtama("Tambah Desa");
    private final JButton btnUbah = Tema.tombolUtama("Simpan Perubahan");
    private final JButton btnHapus = Tema.tombolHapus("Hapus Desa");
    private final JButton btnBatal = Tema.tombol("Batal");

    private final JLabel lblJudulForm = Tema.judul("Tambah Desa", 16f);
    private final JLabel lblSubForm = Tema.redup("Isi data lalu klik Tambah Desa");
    private final JLabel lblJumlah = Tema.redup(" ");
    private final CardLayout modeTombol = new CardLayout();
    private final JPanel panelTombol = Tema.panel(modeTombol);

    private final ListTableModel<Desa> model = new ListTableModel<>(List.of(
            kolom("Kode", String.class, Desa::getKode),
            kolom("Nama Desa", String.class, Desa::getNama),
            kolom("Kabupaten", String.class, (Desa d) -> d.getKabupaten().getNama()),
            kolom("Pagu", BigDecimal.class, Desa::getPagu)));
    private final JTable tabel = Tabel.buat(model);
    private final TableRowSorter<ListTableModel<Desa>> sorter = Tabel.sorter(model);

    private Desa terpilih;

    public DesaPanel(DesaService service) {
        super(new BorderLayout(16, 0));
        this.service = service;
        setOpaque(false);
        tabel.setRowSorter(sorter);

        // ----- form kiri -----
        // Mode tambah: satu aksi utama. Mode ubah: Simpan (utama) + Batal, lalu Hapus dipisah di baris bawah
        // supaya aksi berisiko tidak berdampingan dengan aksi utama.
        JPanel tombolBaru = Tema.panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tombolBaru.add(btnTambah);
        JPanel simpanBatal = Tema.panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        simpanBatal.add(btnUbah);
        simpanBatal.add(javax.swing.Box.createHorizontalStrut(8));
        simpanBatal.add(btnBatal);
        JPanel barisHapus = Tema.panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barisHapus.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.GARIS),
                javax.swing.BorderFactory.createEmptyBorder(14, 0, 0, 0)));
        barisHapus.add(btnHapus);
        JPanel tombolUbah = Tema.panel(new BorderLayout(0, 18));
        tombolUbah.add(simpanBatal, BorderLayout.NORTH);
        tombolUbah.add(barisHapus, BorderLayout.CENTER);
        panelTombol.add(tombolBaru, MODE_BARU);
        panelTombol.add(tombolUbah, MODE_UBAH);

        Form form = new Form()
                .judul(lblJudulForm, lblSubForm)
                .tambah("Kode Desa (10 digit)", txtKode)
                .tambah("Nama Desa", txtNama)
                .tambah("Kabupaten", cmbKabupaten)
                .tambah("Pagu Dana Desa (Rp)", txtPagu)
                .tambahPenuh(panelTombol)
                .selesai();
        Kartu kiri = new Kartu(new BorderLayout());
        kiri.setPreferredSize(new Dimension(360, 0));
        kiri.add(form);

        // ----- tabel kanan -----
        JPanel kepala = Tema.panel(new BorderLayout(12, 0));
        JPanel judul = Tema.panel(new GridLayout(2, 1, 0, 2));
        judul.add(Tema.judul("Daftar Desa", 16f));
        judul.add(lblJumlah);
        txtCari.setPreferredSize(new Dimension(300, 36));
        JPanel bungkusCari = Tema.panel(new BorderLayout());
        bungkusCari.add(txtCari, BorderLayout.NORTH);
        kepala.add(judul, BorderLayout.WEST);
        kepala.add(bungkusCari, BorderLayout.EAST);

        Kartu kanan = new Kartu(new BorderLayout(0, 14));
        kanan.add(kepala, BorderLayout.NORTH);
        kanan.add(Tabel.gulir(tabel), BorderLayout.CENTER);

        add(kiri, BorderLayout.WEST);
        add(kanan, BorderLayout.CENTER);

        // ----- aksi -----
        btnTambah.addActionListener(e -> jalankan(this::tambah));
        btnUbah.addActionListener(e -> jalankan(this::ubah));
        btnHapus.addActionListener(e -> jalankan(this::hapus));
        btnBatal.addActionListener(e -> bersihkan());
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pilihBaris();
            }
        });
        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                saring();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                saring();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                saring();
            }
        });
        aturMode();
    }

    @Override
    public void muatUlang() {
        try {
            Kabupaten kabTerpilih = (Kabupaten) cmbKabupaten.getSelectedItem();
            cmbKabupaten.setModel(new DefaultComboBoxModel<>(service.semuaKabupaten().toArray(Kabupaten[]::new)));
            if (kabTerpilih != null) {
                cmbKabupaten.setSelectedItem(kabTerpilih);
            }
            model.setData(service.semuaDesa());
            perbaruiJumlah();
            bersihkan();
        } catch (RuntimeException e) {
            Pesan.error(this, e);
        }
    }

    // ---------- aksi ----------
    private void tambah() throws AplikasiException {
        Desa d = service.tambah(txtKode.getText(), txtNama.getText(),
                (Kabupaten) cmbKabupaten.getSelectedItem(), txtPagu.getText());
        Pesan.info(this, "Desa " + d.getNama() + " berhasil ditambahkan.");
        muatUlang();
    }

    private void ubah() throws AplikasiException {
        if (terpilih == null) {
            throw new ValidasiException("Pilih desa di tabel terlebih dahulu.");
        }
        service.ubah(terpilih.getId(), txtKode.getText(), txtNama.getText(),
                (Kabupaten) cmbKabupaten.getSelectedItem(), txtPagu.getText());
        Pesan.info(this, "Perubahan data desa berhasil disimpan.");
        muatUlang();
    }

    private void hapus() throws AplikasiException {
        if (terpilih == null) {
            throw new ValidasiException("Pilih desa di tabel terlebih dahulu.");
        }
        if (!Pesan.konfirmasi(this, "Hapus desa " + terpilih.getNama() + "?")) {
            return;
        }
        service.hapus(terpilih);
        Pesan.info(this, "Desa berhasil dihapus.");
        muatUlang();
    }

    // ---------- helper ----------
    private interface Aksi {
        void jalankan() throws AplikasiException;
    }

    /** Semua error ditangkap di sini, sehingga aplikasi tidak pernah berhenti mendadak. */
    private void jalankan(Aksi aksi) {
        try {
            aksi.jalankan();
        } catch (AplikasiException | RuntimeException e) {
            Pesan.error(this, e);
        }
    }

    private void pilihBaris() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            return;
        }
        terpilih = model.getBaris(tabel.convertRowIndexToModel(baris));
        txtKode.setText(terpilih.getKode());
        txtNama.setText(terpilih.getNama());
        cmbKabupaten.setSelectedItem(terpilih.getKabupaten());
        txtPagu.setText(terpilih.getPagu().stripTrailingZeros().toPlainString());
        aturMode();
    }

    private void bersihkan() {
        terpilih = null;
        tabel.clearSelection();
        txtKode.setText("");
        txtNama.setText("");
        txtPagu.setText("");
        aturMode();
        txtKode.requestFocusInWindow();
    }

    private void aturMode() {
        boolean ubah = terpilih != null;
        modeTombol.show(panelTombol, ubah ? MODE_UBAH : MODE_BARU);
        lblJudulForm.setText(ubah ? "Ubah Desa" : "Tambah Desa");
        lblSubForm.setText(ubah ? "Mengubah: " + terpilih.getNama() : "Isi data lalu klik Tambah Desa");
    }

    private void saring() {
        String teks = txtCari.getText().trim();
        sorter.setRowFilter(teks.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(teks)));
        perbaruiJumlah();
    }

    private void perbaruiJumlah() {
        int tampil = tabel.getRowCount();
        int total = model.getRowCount();
        lblJumlah.setText(tampil == total ? total + " desa terdaftar" : tampil + " dari " + total + " desa");
    }
}

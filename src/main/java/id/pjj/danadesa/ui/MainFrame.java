package id.pjj.danadesa.ui;

import id.pjj.danadesa.service.CsvExporter;
import id.pjj.danadesa.service.DesaService;
import id.pjj.danadesa.service.PenyaluranService;
import id.pjj.danadesa.service.RekapService;
import id.pjj.danadesa.ui.komponen.NavItem;
import id.pjj.danadesa.ui.komponen.Tema;
import id.pjj.danadesa.util.Format;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/** Jendela utama: sidebar menu di kiri, judul halaman + konten di kanan. */
public class MainFrame extends JFrame {

    private record Menu(String nama, String judul, String subjudul, JComponent panel, Halaman halaman) {
    }

    private final List<Menu> daftarMenu = new ArrayList<>();
    private final List<NavItem> daftarNav = new ArrayList<>();
    private final CardLayout kartu = new CardLayout();
    private final JPanel konten = Tema.panel(kartu);
    private final JLabel lblJudul = Tema.judul(" ", 24f);
    private final JLabel lblSubjudul = Tema.redup(" ");

    public MainFrame(DesaService desaService, PenyaluranService penyaluranService,
            RekapService rekapService, CsvExporter exporter) {
        super("Pencatatan Penyaluran Dana Desa");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        DashboardPanel dashboard = new DashboardPanel(rekapService);
        DesaPanel desa = new DesaPanel(desaService);
        PenyaluranPanel penyaluran = new PenyaluranPanel(desaService, penyaluranService);
        RekapPanel rekap = new RekapPanel(rekapService, exporter);

        daftarMenu.add(new Menu("Dashboard", "Dashboard",
                "Ringkasan penyaluran Dana Desa seluruh kabupaten", dashboard, dashboard));
        daftarMenu.add(new Menu("Data Desa", "Data Desa",
                "Kelola data desa beserta pagu Dana Desa", desa, desa));
        daftarMenu.add(new Menu("Penyaluran", "Penyaluran",
                "Catat penyaluran Tahap I, II, dan III per desa", penyaluran, penyaluran));
        daftarMenu.add(new Menu("Rekap", "Rekap Penyaluran",
                "Rekap per desa dan per kabupaten, bisa diekspor ke CSV", rekap, rekap));

        JPanel akar = new JPanel(new BorderLayout());
        akar.add(buatSidebar(), BorderLayout.WEST);
        akar.add(buatArea(), BorderLayout.CENTER);
        setContentPane(akar);

        setMinimumSize(new Dimension(1180, 680));
        setSize(1366, 780);
        setLocationRelativeTo(null);
        pilih(0);
    }

    private JComponent buatSidebar() {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(Tema.SIDEBAR);
        s.setBorder(BorderFactory.createEmptyBorder(26, 14, 20, 14));
        s.setPreferredSize(new Dimension(236, 0));

        JLabel logo = new JLabel("Dana Desa");
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 22f));
        logo.setForeground(Color.WHITE);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        JLabel tagline = new JLabel("Pencatatan Penyaluran");
        tagline.setForeground(Tema.SIDEBAR_REDUP);
        tagline.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 0));
        JLabel menu = new JLabel("MENU");
        menu.setFont(menu.getFont().deriveFont(Font.BOLD, 11f));
        menu.setForeground(Tema.SIDEBAR_REDUP);
        menu.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        for (JComponent c : List.of(logo, tagline, menu)) {
            c.setAlignmentX(LEFT_ALIGNMENT);
        }
        s.add(logo);
        s.add(tagline);
        s.add(Box.createVerticalStrut(34));
        s.add(menu);
        s.add(Box.createVerticalStrut(10));

        for (int i = 0; i < daftarMenu.size(); i++) {
            int indeks = i;
            NavItem item = new NavItem(daftarMenu.get(i).nama(), () -> pilih(indeks));
            daftarNav.add(item);
            s.add(item);
            s.add(Box.createVerticalStrut(4));
        }
        s.add(Box.createVerticalGlue());

        JLabel kaki = new JLabel("<html>Capstone PJJ Java<br>Fundamental 2026</html>");
        kaki.setForeground(Tema.SIDEBAR_REDUP);
        kaki.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 0));
        kaki.setAlignmentX(LEFT_ALIGNMENT);
        s.add(kaki);
        return s;
    }

    private JComponent buatArea() {
        JPanel area = new JPanel(new BorderLayout(0, 20));
        area.setBackground(Tema.LATAR);
        area.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel judul = Tema.panel(new BorderLayout());
        JPanel teks = Tema.panel(new java.awt.GridLayout(2, 1, 0, 2));
        teks.add(lblJudul);
        teks.add(lblSubjudul);
        String hariIni = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Format.INDONESIA));
        JLabel tanggal = Tema.redup(hariIni);
        judul.add(teks, BorderLayout.WEST);
        judul.add(tanggal, BorderLayout.EAST);

        for (Menu m : daftarMenu) {
            konten.add(m.panel(), m.nama());
        }
        area.add(judul, BorderLayout.NORTH);
        area.add(konten, BorderLayout.CENTER);
        return area;
    }

    private void pilih(int indeks) {
        Menu m = daftarMenu.get(indeks);
        for (int i = 0; i < daftarNav.size(); i++) {
            daftarNav.get(i).setAktif(i == indeks);
        }
        lblJudul.setText(m.judul());
        lblSubjudul.setText(m.subjudul());
        kartu.show(konten, m.nama());
        m.halaman().muatUlang();
    }
}

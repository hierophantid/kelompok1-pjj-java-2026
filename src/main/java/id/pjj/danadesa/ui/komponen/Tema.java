package id.pjj.danadesa.ui.komponen;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * Tema aplikasi: hanya dua keluarga warna, NAVY dan PUTIH/SILVER.
 * Semua komponen mengambil warna dari sini supaya tampilan konsisten.
 */
public final class Tema {

    // ---- keluarga navy (gelap -> terang) ----
    public static final Color NAVY_900 = new Color(0x0B1B33);   // sidebar
    public static final Color NAVY_800 = new Color(0x132A4D);   // hover sidebar
    public static final Color NAVY_700 = new Color(0x1B3A66);   // warna utama
    public static final Color NAVY_600 = new Color(0x274B7D);   // menu aktif
    public static final Color NAVY_500 = new Color(0x4A6B9B);
    public static final Color NAVY_300 = new Color(0x8FA4C4);
    public static final Color NAVY_100 = new Color(0xDCE4EF);   // seleksi tabel

    // ---- keluarga putih / silver ----
    public static final Color PUTIH = Color.WHITE;
    public static final Color SILVER_50 = new Color(0xF4F6F8);  // latar halaman
    public static final Color SILVER_100 = new Color(0xEBEEF2); // track bilah
    public static final Color SILVER_200 = new Color(0xDDE2E8); // garis/border
    public static final Color SILVER_400 = new Color(0xB4BDC9);
    public static final Color SILVER_600 = new Color(0x6B7A8F); // teks redup

    // ---- alias semantik ----
    public static final Color UTAMA = NAVY_700;
    public static final Color LATAR = SILVER_50;
    public static final Color KARTU = PUTIH;
    public static final Color GARIS = SILVER_200;
    public static final Color TRACK = SILVER_100;
    public static final Color TEKS = new Color(0x0E1E36);
    public static final Color TEKS_REDUP = SILVER_600;
    public static final Color SIDEBAR = NAVY_900;
    public static final Color SIDEBAR_HOVER = NAVY_800;
    public static final Color SIDEBAR_AKTIF = NAVY_600;
    public static final Color SIDEBAR_TEKS = new Color(0xD5DDE8);
    public static final Color SIDEBAR_REDUP = new Color(0x7D8FA9);

    /** Gradasi navy untuk seri grafik (tua -> muda). */
    public static final Color[] SERI = {NAVY_700, NAVY_500, NAVY_300, SILVER_400};

    /** Tinggi standar tombol & isian supaya proporsional. */
    public static final int TINGGI_KONTROL = 34;

    private Tema() {
    }

    /** Dipanggil sekali di awal, sebelum komponen Swing dibuat. */
    public static void pasang() {
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", "#1B3A66"));
        FlatLightLaf.setup();

        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Component.focusColor", NAVY_300);
        UIManager.put("Component.focusedBorderColor", NAVY_700);
        UIManager.put("Component.borderColor", SILVER_200);
        UIManager.put("Button.borderColor", SILVER_400);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ProgressBar.foreground", NAVY_700);

        UIManager.put("Table.rowHeight", 32);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("Table.gridColor", SILVER_100);
        UIManager.put("Table.selectionBackground", NAVY_100);
        UIManager.put("Table.selectionForeground", TEKS);
        UIManager.put("Table.selectionInactiveBackground", NAVY_100);
        UIManager.put("Table.selectionInactiveForeground", TEKS);
        UIManager.put("TableHeader.height", 36);
        UIManager.put("TableHeader.background", SILVER_50);
        UIManager.put("TableHeader.foreground", SILVER_600);
        UIManager.put("TableHeader.separatorColor", PUTIH);
        UIManager.put("TableHeader.bottomSeparatorColor", SILVER_200);

        // ikon dialog ikut warna navy
        UIManager.put("OptionPane.icon.informationColor", NAVY_700);
        UIManager.put("OptionPane.icon.questionColor", NAVY_700);
        UIManager.put("OptionPane.icon.warningColor", NAVY_500);
        UIManager.put("OptionPane.icon.errorColor", NAVY_900);
    }

    /** JPanel transparan, supaya latar kartu putih tidak tertutup. */
    public static JPanel panel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    // ---------- teks ----------
    public static JLabel judul(String teks, float ukuran) {
        JLabel l = new JLabel(teks);
        l.setFont(l.getFont().deriveFont(Font.BOLD, ukuran));
        l.setForeground(TEKS);
        return l;
    }

    public static JLabel redup(String teks) {
        JLabel l = new JLabel(teks);
        l.setForeground(TEKS_REDUP);
        return l;
    }

    public static JLabel label(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(l.getFont().deriveFont(Font.BOLD, l.getFont().getSize2D() - 1f));
        l.setForeground(new Color(0x2B3A52));
        return l;
    }

    // ---------- tombol ----------
    /** Aksi utama (satu per area): navy solid. */
    public static JButton tombolUtama(String teks) {
        JButton b = tombol(teks);
        b.putClientProperty("FlatLaf.style",
                "background:#1B3A66; foreground:#FFFFFF; hoverBackground:#142D50; "
                + "pressedBackground:#0F2340; borderColor:#1B3A66; focusWidth:0; font:bold");
        return b;
    }

    /** Aksi sekunder: putih dengan garis silver, teks navy. */
    public static JButton tombol(String teks) {
        JButton b = new JButton(teks) {
            @Override
            public Dimension getPreferredSize() {
                // tinggi seragam dengan kotak isian; lebar mengikuti teks (minimal 88 px)
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.max(d.width, 88), TINGGI_KONTROL);
            }
        };
        b.setMargin(new Insets(0, 16, 0, 16));
        b.setFocusPainted(false);
        b.putClientProperty("FlatLaf.style",
                "background:#FFFFFF; foreground:#1B3A66; hoverBackground:#F4F6F8; "
                + "pressedBackground:#EBEEF2; borderColor:#B4BDC9; focusWidth:0");
        return b;
    }

    /** Aksi berisiko (hapus): tampil seperti tombol teks agar tidak bersaing dengan aksi utama. */
    public static JButton tombolHapus(String teks) {
        JButton b = tombol(teks);
        b.putClientProperty("FlatLaf.style",
                "background:#FFFFFF; foreground:#0B1B33; hoverBackground:#EBEEF2; "
                + "pressedBackground:#DDE2E8; borderColor:#DDE2E8; focusWidth:0; font:bold");
        return b;
    }

    // ---------- input ----------
    public static JTextField isian(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty("JTextField.placeholderText", placeholder);
        f.setMargin(new Insets(4, 8, 4, 8));
        return f;
    }

    public static JTextField kotakCari(String placeholder) {
        JTextField f = isian(placeholder);
        f.putClientProperty("JTextField.leadingIcon", new FlatSearchIcon());
        f.putClientProperty("JTextField.showClearButton", true);
        return f;
    }
}

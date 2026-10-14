package id.pjj.danadesa.ui.komponen;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.UIManager;

/** Induk semua grafik: antialiasing, font, dan skala sumbu yang "rapi". */
public abstract class Grafik extends JComponent {

    protected static final Locale ID = Locale.forLanguageTag("id-ID");

    protected Grafik() {
        setFont(UIManager.getFont("Label.font"));
        setOpaque(false);
        setToolTipText("");   // mengaktifkan tooltip; teksnya dari getToolTipText(MouseEvent)
    }

    @Override
    protected final void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setFont(getFont());
        gambar(g2);
        g2.dispose();
    }

    protected abstract void gambar(Graphics2D g2);

    protected Font kecil() {
        return getFont().deriveFont(getFont().getSize2D() - 1.5f);
    }

    protected Font tebal(float ukuran) {
        return getFont().deriveFont(Font.BOLD, ukuran);
    }

    protected static void teksTengah(Graphics2D g2, String s, int xTengah, int yBaseline) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(s, xTengah - fm.stringWidth(s) / 2, yBaseline);
    }

    protected static void teksKanan(Graphics2D g2, String s, int xKanan, int yBaseline) {
        g2.drawString(s, xKanan - g2.getFontMetrics().stringWidth(s), yBaseline);
    }

    /** Batas atas sumbu yang bulat (1, 2, 2.5, 5 x 10^n) dengan 4 garis bantu. */
    protected static double skalaRapi(double maks) {
        if (maks <= 0) {
            return 1;
        }
        double kasar = maks / 4;
        double pangkat = Math.pow(10, Math.floor(Math.log10(kasar)));
        double n = kasar / pangkat;
        double langkah = n <= 1 ? 1 : n <= 2 ? 2 : n <= 2.5 ? 2.5 : n <= 5 ? 5 : 10;
        return langkah * pangkat * 4;
    }

    /** Rupiah ringkas untuk label sumbu: "1,5 M", "750 jt". */
    protected static String ringkas(double v) {
        if (v >= 1e9) {
            return String.format(ID, v % 1e9 == 0 ? "%.0f M" : "%.1f M", v / 1e9);
        }
        if (v >= 1e6) {
            return String.format(ID, "%.0f jt", v / 1e6);
        }
        return v == 0 ? "0" : String.format(ID, "%.0f", v);
    }
}

package id.pjj.danadesa.ui.komponen;

import id.pjj.danadesa.util.Format;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 * Bilah persentase berwarna + angka persen.
 * Dipakai sebagai komponen biasa maupun sebagai renderer kolom tabel.
 */
public class BilahPersen extends JComponent implements TableCellRenderer {

    private BigDecimal persen = BigDecimal.ZERO;
    private boolean tampilkanTeks = true;
    private Color latar;
    private Color warnaTetap;

    public BilahPersen() {
        setPreferredSize(new Dimension(140, 22));
        setFont(UIManager.getFont("Label.font"));
    }

    public void setPersen(BigDecimal persen) {
        this.persen = persen == null ? BigDecimal.ZERO : persen;
        repaint();
    }

    /** Pakai satu warna tetap, bukan warna menurut nilai persen. */
    public void setWarnaTetap(Color warna) {
        this.warnaTetap = warna;
    }

    public void setTampilkanTeks(boolean tampil) {
        this.tampilkanTeks = tampil;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        setPersen((BigDecimal) value);
        latar = isSelected ? table.getSelectionBackground() : table.getBackground();
        setFont(table.getFont());
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (latar != null) {
            g2.setColor(latar);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        FontMetrics fm = g2.getFontMetrics(getFont());
        String teks = Format.persen(persen);
        int lebarTeks = tampilkanTeks ? fm.stringWidth("100,00%") + 10 : 0;
        int pad = latar != null ? 8 : 0;
        int tinggi = 8;
        int y = (getHeight() - tinggi) / 2;
        int lebarBar = Math.max(10, getWidth() - lebarTeks - pad * 2);

        g2.setColor(Tema.TRACK);
        g2.fillRoundRect(pad, y, lebarBar, tinggi, tinggi, tinggi);
        double rasio = Math.min(1.0, Math.max(0.0, persen.doubleValue() / 100.0));
        int isi = (int) Math.round(lebarBar * rasio);
        if (isi > 0) {
            g2.setColor(warnaTetap != null ? warnaTetap : Tema.UTAMA);
            g2.fillRoundRect(pad, y, Math.max(isi, tinggi), tinggi, tinggi, tinggi);
        }
        if (tampilkanTeks) {
            g2.setFont(getFont());
            g2.setColor(Tema.TEKS);
            int x = getWidth() - pad - fm.stringWidth(teks);
            g2.drawString(teks, x, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
        }
        g2.dispose();
    }
}

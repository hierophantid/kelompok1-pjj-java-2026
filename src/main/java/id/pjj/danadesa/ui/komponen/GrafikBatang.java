package id.pjj.danadesa.ui.komponen;

import id.pjj.danadesa.util.Format;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.math.BigDecimal;
import java.util.List;

/**
 * Grafik batang horizontal "pagu vs tersalur".
 * Panjang batang silver = pagu (relatif terhadap pagu terbesar), isi navy = tersalur.
 */
public class GrafikBatang extends Grafik {

    public record Item(String label, BigDecimal pagu, BigDecimal tersalur, BigDecimal persen) {
    }

    private List<Item> data = List.of();

    public void setData(List<Item> data) {
        this.data = List.copyOf(data);
        repaint();
    }

    @Override
    protected void gambar(Graphics2D g2) {
        if (data.isEmpty()) {
            return;
        }
        double maksPagu = data.stream().mapToDouble(i -> i.pagu().doubleValue()).max().orElse(1);
        int lebar = getWidth();

        // legenda
        g2.setFont(kecil());
        FontMetrics fk = g2.getFontMetrics();
        g2.setColor(Tema.UTAMA);
        g2.fillRoundRect(0, 4, 10, 10, 3, 3);
        g2.setColor(Tema.TEKS_REDUP);
        g2.drawString("Tersalur", 16, 13);
        int x2 = 16 + fk.stringWidth("Tersalur") + 16;
        g2.setColor(Tema.SILVER_200);
        g2.fillRoundRect(x2, 4, 10, 10, 3, 3);
        g2.setColor(Tema.TEKS_REDUP);
        g2.drawString("Pagu", x2 + 16, 13);

        int y = 40;
        int tinggiBaris = Math.max(70, Math.min(96, (getHeight() - 40) / data.size()));
        for (Item it : data) {
            g2.setFont(tebal(getFont().getSize2D()));
            FontMetrics fb = g2.getFontMetrics();
            g2.setColor(Tema.TEKS);
            g2.drawString(it.label(), 0, y);
            teksKanan(g2, Format.persen(it.persen()), lebar, y);

            int yBar = y + 8;
            int panjangPagu = (int) Math.round(lebar * it.pagu().doubleValue() / maksPagu);
            int panjangSalur = (int) Math.round(lebar * it.tersalur().doubleValue() / maksPagu);
            g2.setColor(Tema.SILVER_200);
            g2.fillRoundRect(0, yBar, panjangPagu, 12, 12, 12);
            if (panjangSalur > 0) {
                g2.setColor(Tema.UTAMA);
                g2.fillRoundRect(0, yBar, Math.max(12, panjangSalur), 12, 12, 12);
            }

            g2.setFont(kecil());
            g2.setColor(Tema.TEKS_REDUP);
            g2.drawString(Format.rupiahRingkas(it.tersalur()) + " dari " + Format.rupiahRingkas(it.pagu()),
                    0, yBar + 12 + fb.getHeight());
            y += tinggiBaris;
        }
    }
}

package id.pjj.danadesa.ui.komponen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.util.List;

/** Grafik donat dengan angka total di tengah dan legenda di bawahnya. */
public class GrafikDonat extends Grafik {

    public record Irisan(String label, long nilai, Color warna) {
    }

    private List<Irisan> data = List.of();
    private String satuan = "";
    private int cx;
    private int cy;
    private int r;

    public void setData(List<Irisan> data, String satuan) {
        this.data = List.copyOf(data);
        this.satuan = satuan;
        repaint();
    }

    private long total() {
        return data.stream().mapToLong(Irisan::nilai).sum();
    }

    @Override
    protected void gambar(Graphics2D g2) {
        if (data.isEmpty()) {
            return;
        }
        int tinggiLegenda = data.size() * 24 + 8;
        int d = Math.max(60, Math.min(getWidth() - 20, getHeight() - tinggiLegenda - 16));
        int tebal = Math.max(14, d / 6);
        r = d / 2;
        cx = getWidth() / 2;
        cy = r + 4;
        long total = total();

        // cincin
        g2.setStroke(new BasicStroke(tebal, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        int rd = r - tebal / 2;
        if (total == 0) {
            g2.setColor(Tema.SILVER_100);
            g2.drawOval(cx - rd, cy - rd, rd * 2, rd * 2);
        } else {
            double mulai = 90;
            for (Irisan i : data) {
                if (i.nilai() == 0) {
                    continue;
                }
                double sudut = 360.0 * i.nilai() / total;
                g2.setColor(i.warna());
                g2.draw(new Arc2D.Double(cx - rd, cy - rd, rd * 2, rd * 2, mulai, -sudut, Arc2D.OPEN));
                mulai -= sudut;
            }
        }

        // angka tengah
        g2.setFont(tebal(getFont().getSize2D() + 12));
        FontMetrics fb = g2.getFontMetrics();
        g2.setColor(Tema.TEKS);
        teksTengah(g2, String.valueOf(total), cx, cy + fb.getAscent() / 2 - 6);
        g2.setFont(kecil());
        g2.setColor(Tema.TEKS_REDUP);
        teksTengah(g2, satuan, cx, cy + fb.getAscent() / 2 + 12);

        // legenda
        int y = cy + r + 26;
        g2.setFont(getFont());
        int kiri = 4;
        int kanan = getWidth() - 4;
        for (Irisan i : data) {
            g2.setColor(i.warna());
            g2.fillRoundRect(kiri, y - 10, 10, 10, 3, 3);
            g2.setColor(Tema.TEKS);
            g2.drawString(i.label(), kiri + 18, y);
            String angka = i.nilai() + " " + satuan;
            g2.setFont(tebal(getFont().getSize2D()));
            teksKanan(g2, angka, kanan, y);
            g2.setFont(getFont());
            y += 24;
        }
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        double dx = e.getX() - cx;
        double dy = e.getY() - cy;
        double jarak = Math.hypot(dx, dy);
        long total = total();
        if (total == 0 || jarak > r || jarak < r * 0.6) {
            return null;
        }
        // sudut searah jarum jam dari arah jam 12
        double sudut = Math.toDegrees(Math.atan2(dx, -dy));
        if (sudut < 0) {
            sudut += 360;
        }
        double kumulatif = 0;
        for (Irisan i : data) {
            kumulatif += 360.0 * i.nilai() / total;
            if (sudut <= kumulatif && i.nilai() > 0) {
                return i.label() + ": " + i.nilai() + " " + satuan
                        + String.format(ID, " (%.0f%%)", 100.0 * i.nilai() / total);
            }
        }
        return null;
    }
}

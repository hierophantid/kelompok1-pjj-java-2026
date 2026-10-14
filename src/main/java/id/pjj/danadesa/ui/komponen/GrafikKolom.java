package id.pjj.danadesa.ui.komponen;

import id.pjj.danadesa.util.Format;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Grafik kolom bertumpuk: satu kolom per kategori (bulan), satu warna per seri (tahap). */
public class GrafikKolom extends Grafik {

    private List<String> kategori = List.of();
    private List<String> keteranganKategori = List.of();
    private List<double[]> nilai = List.of();
    private String[] namaSeri = new String[0];

    // posisi kolom terakhir digambar, untuk tooltip
    private final List<int[]> areaKolom = new ArrayList<>();

    /**
     * @param kategori label sumbu X (mis. "Jan")
     * @param keterangan label panjang untuk tooltip (mis. "Januari 2026")
     * @param nilai satu array per kategori, berisi nilai tiap seri
     */
    public void setData(List<String> kategori, List<String> keterangan, List<double[]> nilai, String[] namaSeri) {
        this.kategori = List.copyOf(kategori);
        this.keteranganKategori = List.copyOf(keterangan);
        this.nilai = List.copyOf(nilai);
        this.namaSeri = namaSeri.clone();
        repaint();
    }

    @Override
    protected void gambar(Graphics2D g2) {
        areaKolom.clear();
        g2.setFont(kecil());
        FontMetrics fm = g2.getFontMetrics();
        int kiri = 52;
        int atas = 34;
        int bawah = getHeight() - 26;
        int kanan = getWidth() - 6;
        int tinggi = bawah - atas;
        if (kategori.isEmpty() || tinggi < 40) {
            return;
        }

        // legenda (kanan atas)
        int lx = kanan;
        for (int s = namaSeri.length - 1; s >= 0; s--) {
            int w = fm.stringWidth(namaSeri[s]);
            lx -= w;
            g2.setColor(Tema.TEKS_REDUP);
            g2.drawString(namaSeri[s], lx, 14);
            lx -= 16;
            g2.setColor(warna(s));
            g2.fillRoundRect(lx, 5, 10, 10, 3, 3);
            lx -= 16;
        }

        double maks = nilai.stream().mapToDouble(GrafikKolom::total).max().orElse(0);
        double batas = skalaRapi(maks);

        // garis bantu + label sumbu Y
        for (int i = 0; i <= 4; i++) {
            int y = bawah - (int) Math.round(tinggi * i / 4.0);
            g2.setColor(i == 0 ? Tema.SILVER_400 : Tema.SILVER_100);
            g2.drawLine(kiri, y, kanan, y);
            g2.setColor(Tema.TEKS_REDUP);
            teksKanan(g2, ringkas(batas * i / 4), kiri - 8, y + fm.getAscent() / 2 - 1);
        }

        // kolom
        double slot = (double) (kanan - kiri) / kategori.size();
        int lebar = (int) Math.max(6, Math.min(34, slot * 0.58));
        for (int k = 0; k < kategori.size(); k++) {
            int xTengah = kiri + (int) Math.round(slot * k + slot / 2);
            int x = xTengah - lebar / 2;
            double[] v = nilai.get(k);
            int yAtas = bawah;
            for (int s = 0; s < v.length; s++) {
                if (v[s] <= 0) {
                    continue;
                }
                int h = (int) Math.round(tinggi * v[s] / batas);
                yAtas -= h;
                g2.setColor(warna(s));
                g2.fillRect(x, yAtas, lebar, h);
            }
            if (yAtas < bawah) {
                // sudut atas membulat
                g2.setColor(warnaSeriTeratas(v));
                g2.fillRoundRect(x, yAtas, lebar, Math.min(8, bawah - yAtas), 6, 6);
            }
            areaKolom.add(new int[]{(int) (kiri + slot * k), (int) (kiri + slot * (k + 1))});
            g2.setColor(Tema.TEKS_REDUP);
            teksTengah(g2, kategori.get(k), xTengah, bawah + 18);
        }
    }

    private Color warna(int seri) {
        return Tema.SERI[seri % Tema.SERI.length];
    }

    private Color warnaSeriTeratas(double[] v) {
        for (int s = v.length - 1; s >= 0; s--) {
            if (v[s] > 0) {
                return warna(s);
            }
        }
        return warna(0);
    }

    private static double total(double[] v) {
        double t = 0;
        for (double d : v) {
            t += d;
        }
        return t;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        for (int k = 0; k < areaKolom.size(); k++) {
            int[] a = areaKolom.get(k);
            if (e.getX() >= a[0] && e.getX() < a[1]) {
                double[] v = nilai.get(k);
                StringBuilder sb = new StringBuilder("<html><b>").append(keteranganKategori.get(k)).append("</b>");
                for (int s = 0; s < v.length; s++) {
                    if (v[s] > 0) {
                        sb.append("<br>").append(namaSeri[s]).append(": ").append(Format.rupiah(BigDecimal.valueOf(v[s])));
                    }
                }
                sb.append("<br>Total: ").append(Format.rupiah(BigDecimal.valueOf(total(v)))).append("</html>");
                return sb.toString();
            }
        }
        return null;
    }
}

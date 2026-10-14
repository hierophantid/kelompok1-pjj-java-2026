package id.pjj.danadesa.ui.komponen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Panel putih bersudut membulat sebagai wadah konten. */
public class Kartu extends JPanel {

    private static final int ARC = 16;
    private Color aksenKiri;
    private Color latar = Tema.KARTU;
    private Color garis = Tema.GARIS;

    public Kartu(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
    }

    /** Garis warna di sisi kiri kartu (dipakai kartu statistik). */
    public void setAksenKiri(Color warna) {
        this.aksenKiri = warna;
        repaint();
    }

    public void setWarna(Color latar, Color garis) {
        this.latar = latar;
        this.garis = garis;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(latar);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        if (aksenKiri != null) {
            g2.setColor(aksenKiri);
            g2.fillRoundRect(0, 0, 8, getHeight() - 1, ARC, ARC);
            g2.setColor(latar);
            g2.fillRect(4, 0, 6, getHeight() - 1);
        }
        g2.setColor(garis);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        g2.dispose();
        super.paintComponent(g);
    }
}

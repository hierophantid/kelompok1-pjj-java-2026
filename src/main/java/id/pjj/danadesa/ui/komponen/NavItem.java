package id.pjj.danadesa.ui.komponen;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.UIManager;

/** Satu item menu di sidebar. */
public class NavItem extends JComponent {

    private final String teks;
    private boolean aktif;
    private boolean hover;

    public NavItem(String teks, Runnable aksi) {
        this.teks = teks;
        setFont(UIManager.getFont("Label.font").deriveFont(Font.PLAIN, 14f));
        setPreferredSize(new Dimension(200, 40));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setAlignmentX(LEFT_ALIGNMENT);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                aksi.run();
            }
        });
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (aktif) {
            g2.setColor(Tema.SIDEBAR_AKTIF);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        } else if (hover) {
            g2.setColor(Tema.SIDEBAR_HOVER);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }
        // penanda kecil di kiri teks
        g2.setColor(aktif ? java.awt.Color.WHITE : Tema.SIDEBAR_REDUP);
        g2.fillRoundRect(16, getHeight() / 2 - 4, 8, 8, 4, 4);

        g2.setFont(aktif ? getFont().deriveFont(Font.BOLD) : getFont());
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(aktif ? java.awt.Color.WHITE : Tema.SIDEBAR_TEKS);
        g2.drawString(teks, 36, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
    }
}

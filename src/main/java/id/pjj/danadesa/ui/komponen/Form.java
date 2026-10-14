package id.pjj.danadesa.ui.komponen;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** Form vertikal: judul, lalu label di atas setiap isian. */
public class Form extends JPanel {

    private int baris = 0;

    public Form() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    public Form judul(String judul, String subjudul) {
        tambahBaris(Tema.judul(judul, 16f), new Insets(0, 0, 2, 0));
        if (subjudul != null) {
            tambahBaris(Tema.redup(subjudul), new Insets(0, 0, 14, 0));
        }
        return this;
    }

    /** Versi dengan label yang bisa diubah teksnya kemudian. */
    public Form judul(javax.swing.JLabel judul, javax.swing.JLabel subjudul) {
        tambahBaris(judul, new Insets(0, 0, 2, 0));
        tambahBaris(subjudul, new Insets(0, 0, 14, 0));
        return this;
    }

    public Form tambah(String label, JComponent field) {
        if (field instanceof javax.swing.JTextField || field instanceof javax.swing.JComboBox) {
            field.setPreferredSize(new java.awt.Dimension(120, 36));
        }
        tambahBaris(Tema.label(label), new Insets(6, 0, 4, 0));
        tambahBaris(field, new Insets(0, 0, 4, 0));
        return this;
    }

    public Form tambahPenuh(JComponent komponen) {
        tambahBaris(komponen, new Insets(12, 0, 0, 0));
        return this;
    }

    /** Ruang kosong di bawah supaya isi form menempel ke atas. */
    public Form selesai() {
        GridBagConstraints g = dasar(new Insets(0, 0, 0, 0));
        g.weighty = 1;
        add(Box.createVerticalGlue(), g);
        return this;
    }

    private void tambahBaris(JComponent c, Insets insets) {
        add(c, dasar(insets));
    }

    private GridBagConstraints dasar(Insets insets) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = baris++;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = insets;
        return g;
    }
}

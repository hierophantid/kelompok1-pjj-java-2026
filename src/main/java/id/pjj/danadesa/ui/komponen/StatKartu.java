package id.pjj.danadesa.ui.komponen;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;

/** Kartu angka ringkasan: judul kecil, nilai besar, keterangan. */
public class StatKartu extends Kartu {

    private final JLabel lblNilai = Tema.judul("-", 22f);
    private final JLabel lblKet = Tema.redup(" ");

    public StatKartu(String judul, Color warna) {
        super(new GridLayout(3, 1, 0, 4));
        setAksenKiri(warna);
        add(Tema.redup(judul));
        add(lblNilai);
        add(lblKet);
    }

    public void setNilai(String nilai, String keterangan) {
        lblNilai.setText(nilai);
        lblKet.setText(keterangan);
    }
}

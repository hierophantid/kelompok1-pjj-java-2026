package id.pjj.danadesa.ui.komponen;

import id.pjj.danadesa.exception.AturanBisnisException;
import id.pjj.danadesa.exception.DataAksesException;
import id.pjj.danadesa.exception.ValidasiException;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/** Satu tempat untuk menampilkan pesan ke pengguna sesuai jenis error. */
public final class Pesan {

    private static final Logger LOG = Logger.getLogger(Pesan.class.getName());

    private Pesan() {
    }

    public static void info(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean konfirmasi(Component induk, String pesan) {
        return JOptionPane.showConfirmDialog(induk, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static void error(Component induk, Throwable e) {
        String judul;
        int jenis;
        String pesan = e.getMessage();
        if (e instanceof ValidasiException) {
            judul = "Input tidak valid";
            jenis = JOptionPane.WARNING_MESSAGE;
        } else if (e instanceof AturanBisnisException) {
            judul = "Ditolak";
            jenis = JOptionPane.WARNING_MESSAGE;
        } else if (e instanceof DataAksesException) {
            judul = "Kesalahan database";
            jenis = JOptionPane.ERROR_MESSAGE;
            LOG.log(Level.WARNING, pesan, e);
        } else {
            judul = "Kesalahan";
            jenis = JOptionPane.ERROR_MESSAGE;
            pesan = "Terjadi kesalahan tak terduga: " + e;
            LOG.log(Level.SEVERE, "Error tak terduga", e);
        }
        JOptionPane.showMessageDialog(induk, pesan, judul, jenis);
    }
}

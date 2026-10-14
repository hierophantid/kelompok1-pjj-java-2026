package id.pjj.danadesa;

import id.pjj.danadesa.config.ConnectionFactory;
import id.pjj.danadesa.config.DatabaseConfig;
import id.pjj.danadesa.dao.DesaDao;
import id.pjj.danadesa.dao.KabupatenDao;
import id.pjj.danadesa.dao.PenyaluranDao;
import id.pjj.danadesa.exception.KonfigurasiException;
import id.pjj.danadesa.service.CsvExporter;
import id.pjj.danadesa.service.DesaService;
import id.pjj.danadesa.service.PenyaluranService;
import id.pjj.danadesa.service.RekapService;
import id.pjj.danadesa.service.aturan.ValidatorPenyaluran;
import id.pjj.danadesa.ui.MainFrame;
import id.pjj.danadesa.ui.komponen.Pesan;
import id.pjj.danadesa.ui.komponen.Tema;
import java.sql.SQLException;
import java.time.Clock;
import java.time.ZoneId;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::mulai);
    }

    private static void mulai() {
        Tema.pasang();

        // Jaring pengaman terakhir: error tak terduga tetap tampil sebagai dialog
        Thread.setDefaultUncaughtExceptionHandler((t, e)
                -> SwingUtilities.invokeLater(() -> Pesan.error(null, e)));

        ConnectionFactory cf;
        try {
            cf = new ConnectionFactory(DatabaseConfig.muat());
            cf.ujiKoneksi();
        } catch (KonfigurasiException e) {
            gagalMulai("Konfigurasi database bermasalah:\n" + e.getMessage());
            return;
        } catch (SQLException e) {
            gagalMulai("Tidak bisa terhubung ke database.\n" + e.getMessage()
                    + "\n\nPastikan server MySQL berjalan, database dana_desa sudah dibuat "
                    + "(jalankan sql/dana_desa.sql), dan isi db.properties benar.");
            return;
        }

        // Rangkai semua lapisan: DAO -> Service -> UI
        DesaDao desaDao = new DesaDao(cf);
        KabupatenDao kabupatenDao = new KabupatenDao(cf);
        PenyaluranDao penyaluranDao = new PenyaluranDao(cf);

        DesaService desaService = new DesaService(desaDao, kabupatenDao, penyaluranDao);
        PenyaluranService penyaluranService = new PenyaluranService(cf, desaDao, penyaluranDao,
                ValidatorPenyaluran.standar(), Clock.system(ZoneId.of("Asia/Jakarta")));
        RekapService rekapService = new RekapService(desaDao, penyaluranDao);

        new MainFrame(desaService, penyaluranService, rekapService, new CsvExporter()).setVisible(true);
    }

    private static void gagalMulai(String pesan) {
        JOptionPane.showMessageDialog(null, pesan, "Aplikasi tidak bisa dimulai", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}

package id.pjj.danadesa.service;

import id.pjj.danadesa.config.ConnectionFactory;
import id.pjj.danadesa.dao.DesaDao;
import id.pjj.danadesa.dao.PenyaluranDao;
import id.pjj.danadesa.exception.AplikasiException;
import id.pjj.danadesa.exception.AturanBisnisException;
import id.pjj.danadesa.exception.DataAksesException;
import id.pjj.danadesa.exception.ValidasiException;
import id.pjj.danadesa.model.Desa;
import id.pjj.danadesa.model.Penyaluran;
import id.pjj.danadesa.model.Tahap;
import id.pjj.danadesa.service.aturan.KonteksPenyaluran;
import id.pjj.danadesa.service.aturan.ValidatorPenyaluran;
import id.pjj.danadesa.util.InputParser;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Pencatatan penyaluran dengan transaksi database:
 * kunci baris desa -> baca riwayat -> validasi aturan -> simpan -> commit.
 * Jika ada aturan yang dilanggar atau error, semua dibatalkan (rollback).
 */
public class PenyaluranService {

    private final ConnectionFactory connectionFactory;
    private final DesaDao desaDao;
    private final PenyaluranDao penyaluranDao;
    private final ValidatorPenyaluran validator;
    private final Clock clock;

    public PenyaluranService(ConnectionFactory cf, DesaDao desaDao, PenyaluranDao penyaluranDao,
            ValidatorPenyaluran validator, Clock clock) {
        this.connectionFactory = cf;
        this.desaDao = desaDao;
        this.penyaluranDao = penyaluranDao;
        this.validator = validator;
        this.clock = clock;
    }

    public List<Penyaluran> riwayat(int desaId) {
        return penyaluranDao.findByDesa(desaId);
    }

    public Penyaluran catat(Desa desa, Tahap tahap, String tanggalTeks, String nilaiTeks)
            throws AplikasiException {
        if (desa == null) {
            throw new ValidasiException("Desa wajib dipilih.");
        }
        if (tahap == null) {
            throw new ValidasiException("Tahap wajib dipilih.");
        }
        LocalDate tanggal = InputParser.tanggal(tanggalTeks, "Tanggal penyaluran");
        BigDecimal nilai = InputParser.rupiah(nilaiTeks, "Nilai penyaluran");
        return catat(new Penyaluran(null, desa.getId(), tahap, tanggal, nilai));
    }

    public Penyaluran catat(Penyaluran baru) throws AplikasiException {
        try (Connection c = connectionFactory.getConnection()) {
            c.setAutoCommit(false);
            try {
                Desa desa = desaDao.findByIdForUpdate(c, baru.getDesaId())
                        .orElseThrow(() -> new AturanBisnisException("Desa tidak ditemukan."));
                List<Penyaluran> riwayat = penyaluranDao.findByDesa(c, desa.getId());
                validator.periksa(new KonteksPenyaluran(desa, riwayat, baru, LocalDate.now(clock)));
                penyaluranDao.insert(c, baru);
                c.commit();
                return baru;
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw DataAksesException.dari(e);
        }
    }

    /** Hanya tahap terakhir yang boleh dihapus, supaya urutan tahap tetap utuh. */
    public void hapus(Penyaluran p) throws AplikasiException {
        try (Connection c = connectionFactory.getConnection()) {
            c.setAutoCommit(false);
            try {
                desaDao.findByIdForUpdate(c, p.getDesaId())
                        .orElseThrow(() -> new AturanBisnisException("Desa tidak ditemukan."));
                Tahap terakhir = penyaluranDao.findByDesa(c, p.getDesaId()).stream()
                        .map(Penyaluran::getTahap)
                        .max(Comparator.naturalOrder())
                        .orElseThrow(() -> new AturanBisnisException("Data penyaluran tidak ditemukan."));
                if (p.getTahap() != terakhir) {
                    throw new AturanBisnisException("Hanya tahap terakhir (" + terakhir.getLabel()
                            + ") yang boleh dihapus.");
                }
                penyaluranDao.delete(c, p.getId());
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw DataAksesException.dari(e);
        }
    }

    public LocalDate hariIni() {
        return LocalDate.now(clock);
    }
}

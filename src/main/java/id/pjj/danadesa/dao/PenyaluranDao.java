package id.pjj.danadesa.dao;

import id.pjj.danadesa.config.ConnectionFactory;
import id.pjj.danadesa.model.Penyaluran;
import id.pjj.danadesa.model.Tahap;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PenyaluranDao extends AbstractJdbcRepository<Penyaluran> {

    public PenyaluranDao(ConnectionFactory cf) {
        super(cf);
    }

    @Override
    protected String namaTabel() {
        return "penyaluran";
    }

    @Override
    protected String sqlSelect() {
        return "SELECT p.id, p.desa_id, p.tahap, p.tanggal, p.nilai FROM penyaluran p";
    }

    @Override
    protected String kolomId() {
        return "p.id";
    }

    @Override
    protected String urutanDefault() {
        return "p.desa_id, p.tahap";
    }

    @Override
    protected RowMapper<Penyaluran> mapper() {
        return rs -> new Penyaluran(
                rs.getInt("id"),
                rs.getInt("desa_id"),
                Tahap.dariUrutan(rs.getInt("tahap")),
                rs.getDate("tanggal").toLocalDate(),
                rs.getBigDecimal("nilai"));
    }

    @Override
    protected String sqlInsert() {
        return "INSERT INTO penyaluran (desa_id, tahap, tanggal, nilai) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected Object[] parameterInsert(Penyaluran p) {
        return new Object[]{p.getDesaId(), p.getTahap().getUrutan(), p.getTanggal(), p.getNilai()};
    }

    @Override
    protected String sqlUpdate() {
        return "UPDATE penyaluran SET desa_id = ?, tahap = ?, tanggal = ?, nilai = ? WHERE id = ?";
    }

    @Override
    protected Object[] parameterUpdate(Penyaluran p) {
        return new Object[]{p.getDesaId(), p.getTahap().getUrutan(), p.getTanggal(), p.getNilai(), p.getId()};
    }

    public List<Penyaluran> findByDesa(int desaId) {
        return denganKoneksi(c -> findByDesa(c, desaId));
    }

    public List<Penyaluran> findByDesa(Connection c, int desaId) throws SQLException {
        return query(c, sqlSelect() + " WHERE p.desa_id = ? ORDER BY p.tahap", desaId);
    }

    public int hitungPerDesa(int desaId) {
        return denganKoneksi(c -> query(c,
                "SELECT COUNT(*) FROM penyaluran WHERE desa_id = ?",
                rs -> rs.getInt(1), desaId).get(0));
    }

    public BigDecimal totalPerDesa(int desaId) {
        return denganKoneksi(c -> query(c,
                "SELECT COALESCE(SUM(nilai), 0) FROM penyaluran WHERE desa_id = ?",
                rs -> rs.getBigDecimal(1), desaId).get(0));
    }
}

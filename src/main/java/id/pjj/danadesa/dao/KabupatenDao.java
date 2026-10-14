package id.pjj.danadesa.dao;

import id.pjj.danadesa.config.ConnectionFactory;
import id.pjj.danadesa.model.Kabupaten;

public class KabupatenDao extends AbstractJdbcRepository<Kabupaten> {

    public KabupatenDao(ConnectionFactory cf) {
        super(cf);
    }

    @Override
    protected String namaTabel() {
        return "kabupaten";
    }

    @Override
    protected String sqlSelect() {
        return "SELECT k.id, k.kode, k.nama FROM kabupaten k";
    }

    @Override
    protected String kolomId() {
        return "k.id";
    }

    @Override
    protected String urutanDefault() {
        return "k.kode";
    }

    @Override
    protected RowMapper<Kabupaten> mapper() {
        return rs -> new Kabupaten(rs.getInt("id"), rs.getString("kode"), rs.getString("nama"));
    }

    @Override
    protected String sqlInsert() {
        return "INSERT INTO kabupaten (kode, nama) VALUES (?, ?)";
    }

    @Override
    protected Object[] parameterInsert(Kabupaten k) {
        return new Object[]{k.getKode(), k.getNama()};
    }

    @Override
    protected String sqlUpdate() {
        return "UPDATE kabupaten SET kode = ?, nama = ? WHERE id = ?";
    }

    @Override
    protected Object[] parameterUpdate(Kabupaten k) {
        return new Object[]{k.getKode(), k.getNama(), k.getId()};
    }
}

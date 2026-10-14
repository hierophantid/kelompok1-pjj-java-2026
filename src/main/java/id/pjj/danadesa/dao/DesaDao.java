package id.pjj.danadesa.dao;

import id.pjj.danadesa.config.ConnectionFactory;
import id.pjj.danadesa.model.Desa;
import id.pjj.danadesa.model.Kabupaten;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class DesaDao extends AbstractJdbcRepository<Desa> {

    public DesaDao(ConnectionFactory cf) {
        super(cf);
    }

    @Override
    protected String namaTabel() {
        return "desa";
    }

    @Override
    protected String sqlSelect() {
        return "SELECT d.id, d.kode, d.nama, d.pagu, "
                + "k.id AS kab_id, k.kode AS kab_kode, k.nama AS kab_nama "
                + "FROM desa d JOIN kabupaten k ON k.id = d.kabupaten_id";
    }

    @Override
    protected String kolomId() {
        return "d.id";
    }

    @Override
    protected String urutanDefault() {
        return "d.kode";
    }

    @Override
    protected RowMapper<Desa> mapper() {
        return rs -> new Desa(
                rs.getInt("id"),
                rs.getString("kode"),
                rs.getString("nama"),
                new Kabupaten(rs.getInt("kab_id"), rs.getString("kab_kode"), rs.getString("kab_nama")),
                rs.getBigDecimal("pagu"));
    }

    @Override
    protected String sqlInsert() {
        return "INSERT INTO desa (kode, nama, kabupaten_id, pagu) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected Object[] parameterInsert(Desa d) {
        return new Object[]{d.getKode(), d.getNama(), d.getKabupaten().getId(), d.getPagu()};
    }

    @Override
    protected String sqlUpdate() {
        return "UPDATE desa SET kode = ?, nama = ?, kabupaten_id = ?, pagu = ? WHERE id = ?";
    }

    @Override
    protected Object[] parameterUpdate(Desa d) {
        return new Object[]{d.getKode(), d.getNama(), d.getKabupaten().getId(), d.getPagu(), d.getId()};
    }

    /**
     * Ambil desa sekaligus mengunci barisnya sampai transaksi selesai,
     * supaya dua penyaluran bersamaan tidak sama-sama lolos cek sisa pagu.
     */
    public Optional<Desa> findByIdForUpdate(Connection c, int id) throws SQLException {
        return query(c, sqlSelect() + " WHERE d.id = ? FOR UPDATE", id).stream().findFirst();
    }

    /** true jika kode sudah dipakai desa lain (idKecuali = id desa yang sedang diubah, boleh null). */
    public boolean kodeSudahDipakai(String kode, Integer idKecuali) {
        return denganKoneksi(c -> !query(c,
                "SELECT id FROM desa WHERE kode = ? AND id <> ?",
                rs -> rs.getInt(1),
                kode, idKecuali == null ? -1 : idKecuali).isEmpty());
    }
}

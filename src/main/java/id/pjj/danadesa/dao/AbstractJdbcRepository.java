package id.pjj.danadesa.dao;

import id.pjj.danadesa.config.ConnectionFactory;
import id.pjj.danadesa.exception.DataAksesException;
import id.pjj.danadesa.model.Entitas;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementasi generik Repository&lt;T&gt; berbasis JDBC.
 * Semua query memakai PreparedStatement; nilai dari pengguna selalu lewat parameter "?".
 * Nama tabel/kolom berasal dari konstanta di subclass, bukan dari input pengguna.
 *
 * Setiap operasi punya dua versi:
 * - tanpa Connection: membuka dan menutup koneksi sendiri;
 * - dengan Connection: dipakai service saat menjalankan transaksi.
 */
public abstract class AbstractJdbcRepository<T extends Entitas> implements Repository<T> {

    protected final ConnectionFactory connectionFactory;

    protected AbstractJdbcRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    // ---------- yang wajib diisi subclass ----------
    protected abstract String namaTabel();

    /** SELECT lengkap (boleh JOIN) tanpa WHERE/ORDER BY. */
    protected abstract String sqlSelect();

    /** Kolom id beserta alias tabel, misalnya "d.id". */
    protected abstract String kolomId();

    protected abstract String urutanDefault();

    protected abstract RowMapper<T> mapper();

    protected abstract String sqlInsert();

    protected abstract Object[] parameterInsert(T e);

    protected abstract String sqlUpdate();

    /** Parameter UPDATE; id harus jadi parameter terakhir. */
    protected abstract Object[] parameterUpdate(T e);

    // ---------- CRUD ----------
    @Override
    public List<T> findAll() {
        return denganKoneksi(c -> query(c, sqlSelect() + " ORDER BY " + urutanDefault()));
    }

    @Override
    public Optional<T> findById(int id) {
        return denganKoneksi(c -> findById(c, id));
    }

    public Optional<T> findById(Connection c, int id) throws SQLException {
        return query(c, sqlSelect() + " WHERE " + kolomId() + " = ?", id).stream().findFirst();
    }

    @Override
    public T insert(T e) {
        return denganKoneksi(c -> insert(c, e));
    }

    public T insert(Connection c, T e) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sqlInsert(), Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, parameterInsert(e));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
        }
        return e;
    }

    @Override
    public void update(T e) {
        denganKoneksi(c -> eksekusi(c, sqlUpdate(), parameterUpdate(e)));
    }

    @Override
    public void delete(int id) {
        denganKoneksi(c -> delete(c, id));
    }

    public int delete(Connection c, int id) throws SQLException {
        return eksekusi(c, "DELETE FROM " + namaTabel() + " WHERE id = ?", id);
    }

    // ---------- helper untuk subclass ----------
    protected List<T> query(Connection c, String sql, Object... params) throws SQLException {
        return query(c, sql, mapper(), params);
    }

    protected <R> List<R> query(Connection c, String sql, RowMapper<R> m, Object... params)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<R> hasil = new ArrayList<>();
                while (rs.next()) {
                    hasil.add(m.map(rs));
                }
                return hasil;
            }
        }
    }

    protected int eksekusi(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        }
    }

    /** Membuka koneksi, menjalankan aksi, lalu menutup koneksi otomatis. */
    protected <R> R denganKoneksi(SqlFunction<Connection, R> aksi) {
        try (Connection c = connectionFactory.getConnection()) {
            return aksi.apply(c);
        } catch (SQLException e) {
            throw DataAksesException.dari(e);
        }
    }

    protected static void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            if (p instanceof LocalDate tgl) {
                ps.setDate(i + 1, Date.valueOf(tgl));
            } else {
                ps.setObject(i + 1, p);
            }
        }
    }
}

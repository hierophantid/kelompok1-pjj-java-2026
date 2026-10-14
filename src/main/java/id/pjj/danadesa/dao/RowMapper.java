package id.pjj.danadesa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Mengubah satu baris ResultSet menjadi objek T. */
@FunctionalInterface
public interface RowMapper<T> {

    T map(ResultSet rs) throws SQLException;
}

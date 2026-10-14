package id.pjj.danadesa.dao;

import java.sql.SQLException;

/** Seperti java.util.function.Function, tetapi boleh melempar SQLException. */
@FunctionalInterface
public interface SqlFunction<A, R> {

    R apply(A a) throws SQLException;
}

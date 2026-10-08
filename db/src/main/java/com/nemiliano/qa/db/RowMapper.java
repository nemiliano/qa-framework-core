package com.nemiliano.qa.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Convierte la fila actual de un {@link ResultSet} en un objeto. No llamar a {@code next()}. */
@FunctionalInterface
public interface RowMapper<T> {

  T map(ResultSet rs) throws SQLException;
}

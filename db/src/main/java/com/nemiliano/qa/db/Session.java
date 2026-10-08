package com.nemiliano.qa.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Operaciones sobre una conexión concreta. Todo se ejecuta con {@link PreparedStatement}: los
 * valores van siempre como parámetros, nunca concatenados al SQL.
 */
public final class Session {

  private final Connection connection;

  Session(Connection connection) {
    this.connection = connection;
  }

  public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
    try (PreparedStatement statement = prepare(sql, params);
        ResultSet rs = statement.executeQuery()) {
      List<T> result = new ArrayList<>();
      while (rs.next()) {
        result.add(mapper.map(rs));
      }
      return result;
    } catch (SQLException e) {
      throw new DbException("Falló la consulta: " + sql, e);
    }
  }

  /** Devuelve la única fila esperada; falla si hay más de una. */
  public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
    List<T> rows = query(sql, mapper, params);
    if (rows.size() > 1) {
      throw new DbException(
          "Se esperaba una fila y hubo " + rows.size() + ": " + sql, new IllegalStateException());
    }
    return rows.stream().findFirst();
  }

  /** INSERT / UPDATE / DELETE / DDL. Devuelve la cantidad de filas afectadas. */
  public int update(String sql, Object... params) {
    try (PreparedStatement statement = prepare(sql, params)) {
      return statement.executeUpdate();
    } catch (SQLException e) {
      throw new DbException("Falló la sentencia: " + sql, e);
    }
  }

  private PreparedStatement prepare(String sql, Object[] params) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(sql);
    try {
      for (int i = 0; i < params.length; i++) {
        statement.setObject(i + 1, params[i]);
      }
      return statement;
    } catch (SQLException e) {
      statement.close();
      throw e;
    }
  }
}

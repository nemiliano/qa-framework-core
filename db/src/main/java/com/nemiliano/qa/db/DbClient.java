package com.nemiliano.qa.db;

import com.nemiliano.qa.core.config.QaConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Acceso a base de datos con pool de conexiones. Funciona con cualquier motor JDBC: el driver
 * (Oracle, PostgreSQL, H2...) lo aporta el proyecto que usa este módulo.
 */
public final class DbClient implements AutoCloseable {

  private final HikariDataSource dataSource;

  private DbClient(HikariDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public static DbClient create(String jdbcUrl, String user, String password) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(user);
    config.setPassword(password);
    config.setMaximumPoolSize(4); // alcanza para tests en paralelo con pocos hilos
    config.setPoolName("qa-db");
    return new DbClient(new HikariDataSource(config));
  }

  public static DbClient fromConfig(QaConfig config) {
    return create(config.dbUrl(), config.dbUser(), config.dbPassword());
  }

  public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
    return inSession(session -> session.query(sql, mapper, params));
  }

  public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
    return inSession(session -> session.queryOne(sql, mapper, params));
  }

  public int update(String sql, Object... params) {
    return inSession(session -> session.update(sql, params));
  }

  /**
   * Ejecuta varias operaciones en una transacción: commit si termina bien, rollback si lanza una
   * excepción.
   */
  public <T> T transaction(Function<Session, T> work) {
    try (Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);
      try {
        T result = work.apply(new Session(connection));
        connection.commit();
        return result;
      } catch (RuntimeException e) {
        connection.rollback();
        throw e;
      }
    } catch (SQLException e) {
      throw new DbException("Falló la transacción", e);
    }
  }

  private <T> T inSession(Function<Session, T> work) {
    try (Connection connection = dataSource.getConnection()) {
      return work.apply(new Session(connection));
    } catch (SQLException e) {
      throw new DbException("No se pudo obtener una conexión", e);
    }
  }

  @Override
  public void close() {
    dataSource.close();
  }
}

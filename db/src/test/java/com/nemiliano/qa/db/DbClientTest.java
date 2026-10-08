package com.nemiliano.qa.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DbClientTest {

  private DbClient db;

  @BeforeEach
  void setUp() {
    // Base H2 en memoria, distinta por test: tests independientes y paralelizables
    db = DbClient.create("jdbc:h2:mem:" + UUID.randomUUID(), "sa", "");
    db.update("CREATE TABLE usuario (id INT PRIMARY KEY, nombre VARCHAR(50) NOT NULL)");
  }

  @AfterEach
  void tearDown() {
    db.close();
  }

  @Test
  @DisplayName("Inserta y consulta con parámetros")
  void insertsAndQueries() {
    db.update("INSERT INTO usuario (id, nombre) VALUES (?, ?)", 1, "Ana");
    db.update("INSERT INTO usuario (id, nombre) VALUES (?, ?)", 2, "Luis");

    var names = db.query("SELECT nombre FROM usuario ORDER BY id", rs -> rs.getString("nombre"));

    assertThat(names).containsExactly("Ana", "Luis");
  }

  @Test
  @DisplayName("queryOne devuelve vacío si no hay filas")
  void queryOneReturnsEmpty() {
    assertThat(db.queryOne("SELECT nombre FROM usuario WHERE id = ?", rs -> rs.getString(1), 99))
        .isEmpty();
  }

  @Test
  @DisplayName("Un valor con comillas no rompe el SQL (no hay inyección)")
  void parametersAreNotConcatenated() {
    String hostile = "x'); DROP TABLE usuario; --";
    db.update("INSERT INTO usuario (id, nombre) VALUES (?, ?)", 1, hostile);

    assertThat(db.queryOne("SELECT nombre FROM usuario WHERE id = 1", rs -> rs.getString(1)))
        .contains(hostile);
  }

  @Test
  @DisplayName("La transacción hace rollback si algo falla")
  void transactionRollsBackOnError() {
    assertThatThrownBy(
            () ->
                db.transaction(
                    session -> {
                      session.update("INSERT INTO usuario (id, nombre) VALUES (?, ?)", 1, "Ana");
                      throw new IllegalStateException("falla a mitad de camino");
                    }))
        .isInstanceOf(IllegalStateException.class);

    assertThat(db.query("SELECT id FROM usuario", rs -> rs.getInt(1))).isEmpty();
  }

  @Test
  @DisplayName("La transacción hace commit si termina bien")
  void transactionCommits() {
    db.transaction(
        session -> {
          session.update("INSERT INTO usuario (id, nombre) VALUES (?, ?)", 1, "Ana");
          return session.update("INSERT INTO usuario (id, nombre) VALUES (?, ?)", 2, "Luis");
        });

    assertThat(db.query("SELECT id FROM usuario", rs -> rs.getInt(1))).hasSize(2);
  }

  @Test
  @DisplayName("SQL inválido lanza DbException con el SQL en el mensaje")
  void badSqlThrowsDbException() {
    assertThatThrownBy(() -> db.query("SELECT * FROM tabla_inexistente", rs -> rs.getInt(1)))
        .isInstanceOf(DbException.class)
        .hasMessageContaining("tabla_inexistente");
  }
}

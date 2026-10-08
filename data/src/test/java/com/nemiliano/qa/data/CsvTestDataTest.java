package com.nemiliano.qa.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;

/** Prueba de punta a punta de la anotación: el nombre de cada iteración es el Caso de prueba. */
class CsvTestDataTest {

  @ParameterizedTest(name = "{0}")
  @CsvTestData(file = "data/login.csv", type = LoginData.class)
  void csvRowsArriveAsRecords(LoginData data) {
    assertThat(data.usuario()).isNotBlank();
    assertThat(data.resultadoEsperado()).isNotBlank();
  }

  @ParameterizedTest(name = "{0}")
  @JsonTestData(file = "data/login.json", type = PersonData.class)
  void jsonRowsArriveAsRecords(PersonData data) {
    assertThat(data.direccion().cp()).hasSize(4);
  }
}

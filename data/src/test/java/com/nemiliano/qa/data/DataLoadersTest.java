package com.nemiliano.qa.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DataLoadersTest {

  @Test
  @DisplayName("CSV: mapea cada fila a un record tipado, con tildes y booleanos")
  void csvMapsRowsToRecords() {
    List<LoginData> rows = CsvDataLoader.load("data/login.csv", LoginData.class);

    assertThat(rows).hasSize(3);
    assertThat(rows.get(0).casoDePrueba()).isEqualTo("Usuario válido accede al inventario");
    assertThat(rows.get(0).exito()).isTrue();
    assertThat(rows.get(1).exito()).isFalse();
  }

  @Test
  @DisplayName("JSON: soporta datos anidados")
  void jsonSupportsNestedData() {
    List<PersonData> rows = JsonDataLoader.load("data/login.json", PersonData.class);

    assertThat(rows).hasSize(2);
    assertThat(rows.get(0).direccion().ciudad()).isEqualTo("Rosario");
    assertThat(rows.get(1).edad()).isEqualTo(17);
  }

  @Test
  @DisplayName("Falla con mensaje claro si falta una columna")
  void missingColumnFails() {
    assertThatThrownBy(() -> CsvDataLoader.load("data/invalid/missing-column.csv", LoginData.class))
        .isInstanceOf(DataException.class)
        .hasMessageContaining("faltan columnas [clave]")
        .hasMessageContaining("missing-column.csv");
  }

  @Test
  @DisplayName("Falla indicando fila y columna si un valor no tiene el tipo esperado")
  void badValueFails() {
    assertThatThrownBy(() -> CsvDataLoader.load("data/invalid/bad-boolean.csv", LoginData.class))
        .isInstanceOf(DataException.class)
        .hasMessageContaining("fila 2")
        .hasMessageContaining("columna 'exito'")
        .hasMessageContaining("quizas");
  }

  @Test
  @DisplayName("Falla si el Caso de prueba está repetido")
  void duplicateCaseFails() {
    assertThatThrownBy(() -> CsvDataLoader.load("data/invalid/duplicate-case.csv", LoginData.class))
        .isInstanceOf(DataException.class)
        .hasMessageContaining("repetido");
  }

  @Test
  @DisplayName("Falla si el Caso de prueba está vacío")
  void blankCaseFails() {
    assertThatThrownBy(() -> CsvDataLoader.load("data/invalid/blank-case.csv", LoginData.class))
        .isInstanceOf(DataException.class)
        .hasMessageContaining("no tiene 'Caso de prueba'");
  }

  @Test
  @DisplayName("Falla si el archivo no existe")
  void missingFileFails() {
    assertThatThrownBy(() -> CsvDataLoader.load("data/nada.csv", LoginData.class))
        .isInstanceOf(DataException.class)
        .hasMessageContaining("No existe el archivo de datos");
  }

  @Test
  @DisplayName("Las claves no llegan al reporte")
  void sensitiveFieldsAreMasked() {
    assertThat(TestCaseReportingExtension.isSensitive("clave")).isTrue();
    assertThat(TestCaseReportingExtension.isSensitive("usuario")).isFalse();
  }
}

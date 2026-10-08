package com.nemiliano.qa.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Validaciones comunes a cualquier formato de datos. */
final class DataValidator {

  private DataValidator() {}

  static <T extends TestCase> void requireRecordOfTestCase(Class<T> type) {
    if (!type.isRecord()) {
      throw new DataException(type.getName() + " debe ser un record de Java");
    }
    if (!TestCase.class.isAssignableFrom(type)) {
      throw new DataException(
          type.getName() + " debe implementar TestCase (necesita el componente casoDePrueba)");
    }
  }

  /** El caso de prueba es obligatorio y único: se usa para nombrar tests y evidencias. */
  static <T extends TestCase> List<T> validate(List<T> rows, String source) {
    if (rows.isEmpty()) {
      throw new DataException("El archivo de datos '" + source + "' no tiene filas");
    }
    Set<String> seen = new HashSet<>();
    int index = 1;
    for (T row : rows) {
      String caso = row.casoDePrueba();
      if (caso == null || caso.isBlank()) {
        throw new DataException(
            "'" + source + "': la fila " + index + " no tiene 'Caso de prueba'");
      }
      if (!seen.add(caso)) {
        throw new DataException(
            "'" + source + "': el 'Caso de prueba' está repetido: \"" + caso + "\"");
      }
      index++;
    }
    return rows;
  }
}

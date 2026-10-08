package com.nemiliano.qa.data;

/**
 * Todo record de datos de prueba lo implementa. {@code casoDePrueba} es solo metadata: describe el
 * escenario que valida la fila y se usa para el nombre del test, Allure, logs y evidencias. Nunca
 * participa de las validaciones.
 */
public interface TestCase {

  String casoDePrueba();
}

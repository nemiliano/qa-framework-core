package com.nemiliano.qa.data;

/** Fila de ejemplo para los tests del módulo. */
public record LoginData(
    String casoDePrueba, String usuario, String clave, boolean exito, String resultadoEsperado)
    implements TestCase {}

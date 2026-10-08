package com.nemiliano.qa.data;

/** Fila de ejemplo con datos anidados (JSON). */
public record PersonData(String casoDePrueba, String nombre, Address direccion, int edad)
    implements TestCase {

  public record Address(String ciudad, String cp) {}
}

package com.nemiliano.qa.reporting;

/** Arma nombres de archivo de evidencia: clase, método y caso de prueba. */
public final class EvidenceNamer {

  private static final int MAX_LENGTH = 120;

  private EvidenceNamer() {}

  /**
   * Ejemplo: {@code LoginTest_validUser_Usuario_valido}. Solo deja letras, números, punto, guion y
   * guion bajo para que sea válido en cualquier sistema de archivos.
   */
  public static String baseName(String className, String methodName, String displayName) {
    String raw = className + "_" + methodName + "_" + displayName;
    String clean = raw.replaceAll("[^\\p{L}\\p{N}._-]+", "_").replaceAll("_+", "_");
    clean = clean.replaceAll("^_|_$", "");
    return clean.length() > MAX_LENGTH ? clean.substring(0, MAX_LENGTH) : clean;
  }
}

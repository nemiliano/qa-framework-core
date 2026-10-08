package com.nemiliano.qa.reporting;

import com.nemiliano.qa.core.exception.QaFrameworkException;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Guarda evidencias en disco y las adjunta al reporte Allure. */
public final class Evidence {

  private Evidence() {}

  /** Guarda {@code <baseName>_<suffix>.<extension>} dentro de {@code dir}. */
  public static Path save(
      Path dir, String baseName, String suffix, String extension, byte[] content) {
    try {
      Files.createDirectories(dir);
      Path file = dir.resolve(baseName + "_" + suffix + "." + extension);
      Files.write(file, content);
      return file;
    } catch (IOException e) {
      throw new QaFrameworkException("No se pudo guardar la evidencia en " + dir, e);
    }
  }

  /** Adjunta bytes al test que se está ejecutando en Allure. */
  public static void attach(String name, String mimeType, String extension, byte[] content) {
    Allure.addAttachment(name, mimeType, new ByteArrayInputStream(content), extension);
  }

  public static void attachText(String name, String text) {
    attach(name, "text/plain", "txt", text.getBytes(StandardCharsets.UTF_8));
  }

  /** Guarda en disco y adjunta a Allure con un solo llamado. */
  public static void saveAndAttach(
      Path dir,
      String baseName,
      String suffix,
      String attachmentName,
      String mimeType,
      String extension,
      byte[] content) {
    save(dir, baseName, suffix, extension, content);
    attach(attachmentName, mimeType, extension, content);
  }

  /** Adjunta un archivo ya existente (por ejemplo, el trace.zip de Playwright). */
  public static void attachFile(String name, String mimeType, Path file) {
    try {
      attach(name, mimeType, extensionOf(file), Files.readAllBytes(file));
    } catch (IOException e) {
      throw new QaFrameworkException("No se pudo leer la evidencia " + file, e);
    }
  }

  private static String extensionOf(Path file) {
    String name = file.getFileName().toString();
    int dot = name.lastIndexOf('.');
    return dot < 0 ? "bin" : name.substring(dot + 1);
  }
}

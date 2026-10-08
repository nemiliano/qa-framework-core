package com.nemiliano.qa.files;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nemiliano.qa.core.exception.QaFrameworkException;
import java.io.IOException;
import java.nio.file.Path;

/** Lectura y escritura de archivos JSON. */
public final class JsonFiles {

  private static final ObjectMapper MAPPER =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  private JsonFiles() {}

  public static <T> T read(Path file, Class<T> type) {
    try {
      return MAPPER.readValue(file.toFile(), type);
    } catch (IOException e) {
      throw new QaFrameworkException("No se pudo leer el JSON " + file, e);
    }
  }

  public static void write(Path file, Object value) {
    try {
      MAPPER.writeValue(file.toFile(), value);
    } catch (IOException e) {
      throw new QaFrameworkException("No se pudo escribir el JSON " + file, e);
    }
  }
}

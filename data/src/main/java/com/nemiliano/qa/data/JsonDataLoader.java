package com.nemiliano.qa.data;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Lee un JSON del classpath con un arreglo de objetos y arma un record por elemento. Sirve para
 * datos anidados. Es estricto: propiedades desconocidas o faltantes fallan con mensaje claro.
 */
public final class JsonDataLoader {

  private static final ObjectMapper MAPPER =
      new ObjectMapper()
          .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
          .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);

  private JsonDataLoader() {}

  public static <T extends TestCase> List<T> load(String resource, Class<T> type) {
    DataValidator.requireRecordOfTestCase(type);
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try (InputStream in = cl == null ? null : cl.getResourceAsStream(resource)) {
      if (in == null) {
        throw new DataException("No existe el archivo de datos '" + resource + "' en el classpath");
      }
      List<T> rows =
          MAPPER.readValue(in, MAPPER.getTypeFactory().constructCollectionType(List.class, type));
      return DataValidator.validate(rows, resource);
    } catch (JacksonException e) {
      throw new DataException(
          "'"
              + resource
              + "' no es válido para "
              + type.getSimpleName()
              + ": "
              + e.getOriginalMessage(),
          e);
    } catch (IOException e) {
      throw new DataException("No se pudo leer el archivo de datos '" + resource + "'", e);
    }
  }
}

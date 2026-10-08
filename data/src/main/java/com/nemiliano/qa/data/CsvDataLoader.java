package com.nemiliano.qa.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Lee un CSV del classpath (UTF-8) y arma un record tipado por fila.
 *
 * <p>Las columnas se asocian al componente del record por nombre ignorando mayúsculas, espacios,
 * guiones y acentos: la columna "Caso de prueba" se asocia a {@code casoDePrueba}.
 */
public final class CsvDataLoader {

  private CsvDataLoader() {}

  public static <T extends TestCase> List<T> load(String resource, Class<T> type) {
    DataValidator.requireRecordOfTestCase(type);
    try (InputStream in = open(resource);
        Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        CSVParser parser =
            CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .get()
                .parse(reader)) {

      Map<String, String> headerByNormalized = headers(parser);
      RecordComponent[] components = type.getRecordComponents();
      checkRequiredColumns(resource, components, headerByNormalized);

      List<T> rows = new ArrayList<>();
      for (CSVRecord csvRecord : parser) {
        rows.add(build(type, components, headerByNormalized, csvRecord, resource));
      }
      return DataValidator.validate(rows, resource);
    } catch (IOException e) {
      throw new DataException("No se pudo leer el archivo de datos '" + resource + "'", e);
    }
  }

  private static InputStream open(String resource) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream in = cl == null ? null : cl.getResourceAsStream(resource);
    if (in == null) {
      in = CsvDataLoader.class.getClassLoader().getResourceAsStream(resource);
    }
    if (in == null) {
      throw new DataException("No existe el archivo de datos '" + resource + "' en el classpath");
    }
    return in;
  }

  private static Map<String, String> headers(CSVParser parser) {
    Map<String, String> map = new HashMap<>();
    for (String header : parser.getHeaderNames()) {
      map.put(normalize(header), header);
    }
    return map;
  }

  private static void checkRequiredColumns(
      String resource, RecordComponent[] components, Map<String, String> headers) {
    List<String> missing = new ArrayList<>();
    for (RecordComponent c : components) {
      if (!headers.containsKey(normalize(c.getName()))) {
        missing.add(c.getName());
      }
    }
    if (!missing.isEmpty()) {
      throw new DataException(
          "'"
              + resource
              + "': faltan columnas "
              + missing
              + ". Columnas encontradas: "
              + headers.values());
    }
  }

  private static <T> T build(
      Class<T> type,
      RecordComponent[] components,
      Map<String, String> headers,
      CSVRecord csvRecord,
      String resource) {
    Object[] args = new Object[components.length];
    Class<?>[] types = new Class<?>[components.length];
    for (int i = 0; i < components.length; i++) {
      RecordComponent c = components[i];
      types[i] = c.getType();
      String column = headers.get(normalize(c.getName()));
      String raw = csvRecord.isSet(column) ? csvRecord.get(column) : "";
      try {
        args[i] = convert(raw, c.getType());
      } catch (IllegalArgumentException | DateTimeParseException e) {
        throw new DataException(
            "'"
                + resource
                + "' fila "
                + (csvRecord.getRecordNumber() + 1)
                + ", columna '"
                + column
                + "': valor \""
                + raw
                + "\" no es válido para "
                + c.getType().getSimpleName());
      }
    }
    try {
      Constructor<T> constructor = type.getDeclaredConstructor(types);
      constructor.setAccessible(true);
      return constructor.newInstance(args);
    } catch (ReflectiveOperationException e) {
      throw new DataException("No se pudo crear " + type.getSimpleName() + " desde " + resource, e);
    }
  }

  static Object convert(String raw, Class<?> type) {
    if (type == String.class) {
      return raw;
    }
    if (raw.isEmpty()) {
      if (type.isPrimitive()) {
        throw new IllegalArgumentException("valor vacío para tipo primitivo");
      }
      return null;
    }
    if (type == int.class || type == Integer.class) {
      return Integer.valueOf(raw);
    }
    if (type == long.class || type == Long.class) {
      return Long.valueOf(raw);
    }
    if (type == double.class || type == Double.class) {
      return Double.valueOf(raw);
    }
    if (type == BigDecimal.class) {
      return new BigDecimal(raw);
    }
    if (type == boolean.class || type == Boolean.class) {
      if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) {
        return Boolean.valueOf(raw);
      }
      throw new IllegalArgumentException("se esperaba true o false");
    }
    if (type == LocalDate.class) {
      return LocalDate.parse(raw);
    }
    throw new IllegalArgumentException("tipo no soportado: " + type.getSimpleName());
  }

  /** "Caso de prueba", "caso_de_prueba" y "casoDePrueba" dan todos "casodeprueba". */
  static String normalize(String name) {
    String noAccents = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
    return noAccents.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
  }
}

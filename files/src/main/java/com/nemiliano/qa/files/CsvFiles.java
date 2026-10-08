package com.nemiliano.qa.files;

import com.nemiliano.qa.core.exception.QaFrameworkException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 * Lectura y escritura de CSV genéricos (por ejemplo un reporte descargado). Para datos de prueba
 * tipados usar el módulo {@code data}.
 */
public final class CsvFiles {

  private CsvFiles() {}

  /** Cada fila como mapa columna a valor, respetando el orden de las columnas. */
  public static List<Map<String, String>> read(Path file) {
    try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
        CSVParser parser =
            CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .get()
                .parse(reader)) {
      List<Map<String, String>> rows = new ArrayList<>();
      for (CSVRecord csvRecord : parser) {
        rows.add(new LinkedHashMap<>(csvRecord.toMap()));
      }
      return rows;
    } catch (IOException e) {
      throw new QaFrameworkException("No se pudo leer el CSV " + file, e);
    }
  }

  public static void write(Path file, List<String> headers, List<List<String>> rows) {
    try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
        CSVPrinter printer =
            new CSVPrinter(
                writer,
                CSVFormat.DEFAULT.builder().setHeader(headers.toArray(String[]::new)).get())) {
      for (List<String> row : rows) {
        printer.printRecord(row);
      }
    } catch (IOException e) {
      throw new QaFrameworkException("No se pudo escribir el CSV " + file, e);
    }
  }
}

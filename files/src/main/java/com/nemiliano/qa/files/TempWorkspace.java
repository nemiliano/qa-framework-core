package com.nemiliano.qa.files;

import com.nemiliano.qa.core.exception.QaFrameworkException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Carpeta temporal propia de un test. Se borra sola al cerrar (try-with-resources), así los tests
 * no dejan basura ni se pisan entre sí.
 */
public final class TempWorkspace implements AutoCloseable {

  private final Path root;

  private TempWorkspace(Path root) {
    this.root = root;
  }

  public static TempWorkspace create() {
    try {
      return new TempWorkspace(Files.createTempDirectory("qa-"));
    } catch (IOException e) {
      throw new UncheckedIOException("No se pudo crear la carpeta temporal", e);
    }
  }

  public Path root() {
    return root;
  }

  public Path resolve(String name) {
    return root.resolve(name);
  }

  public Path writeText(String name, String content) {
    try {
      return Files.writeString(resolve(name), content, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new QaFrameworkException("No se pudo escribir " + name, e);
    }
  }

  @Override
  public void close() {
    try (Stream<Path> paths = Files.walk(root)) {
      paths.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
    } catch (IOException e) {
      throw new UncheckedIOException("No se pudo borrar " + root, e);
    }
  }
}

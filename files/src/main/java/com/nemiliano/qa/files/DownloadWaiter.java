package com.nemiliano.qa.files;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.awaitility.Awaitility;

/** Espera a que un navegador termine de descargar un archivo. */
public final class DownloadWaiter {

  /** Extensiones que los navegadores usan mientras la descarga sigue en curso. */
  private static final List<String> PARTIAL = List.of(".crdownload", ".part", ".tmp");

  private DownloadWaiter() {}

  /**
   * Devuelve el primer archivo completo con la extensión indicada (por ejemplo {@code "pdf"}).
   * Falla con un mensaje claro si no aparece dentro del tiempo dado.
   */
  public static Path waitForFile(Path dir, String extension, Duration timeout) {
    String suffix = "." + extension.toLowerCase();
    return Awaitility.await("descarga de *" + suffix + " en " + dir)
        .atMost(timeout)
        .pollInterval(Duration.ofMillis(200))
        .until(() -> find(dir, suffix), Optional::isPresent)
        .orElseThrow();
  }

  private static Optional<Path> find(Path dir, String suffix) {
    if (!Files.isDirectory(dir)) {
      return Optional.empty();
    }
    try (Stream<Path> files = Files.list(dir)) {
      return files
          .filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().toLowerCase().endsWith(suffix))
          .filter(p -> PARTIAL.stream().noneMatch(p.getFileName().toString()::endsWith))
          .findFirst();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}

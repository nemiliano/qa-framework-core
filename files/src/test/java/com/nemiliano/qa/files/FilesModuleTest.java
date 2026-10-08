package com.nemiliano.qa.files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilesModuleTest {

  record Persona(String nombre, int edad) {}

  @Test
  @DisplayName("TempWorkspace borra la carpeta al cerrar")
  void tempWorkspaceCleansUp() {
    Path root;
    try (TempWorkspace workspace = TempWorkspace.create()) {
      root = workspace.root();
      workspace.writeText("a.txt", "hola");
      assertThat(Files.exists(workspace.resolve("a.txt"))).isTrue();
    }
    assertThat(Files.exists(root)).isFalse();
  }

  @Test
  @DisplayName("CSV: escribe y lee con acentos")
  void csvRoundTrip() {
    try (TempWorkspace workspace = TempWorkspace.create()) {
      Path file = workspace.resolve("reporte.csv");
      CsvFiles.write(file, List.of("nombre", "ciudad"), List.of(List.of("José", "Córdoba")));

      assertThat(CsvFiles.read(file))
          .containsExactly(Map.of("nombre", "José", "ciudad", "Córdoba"));
    }
  }

  @Test
  @DisplayName("JSON: escribe y lee un record")
  void jsonRoundTrip() {
    try (TempWorkspace workspace = TempWorkspace.create()) {
      Path file = workspace.resolve("persona.json");
      JsonFiles.write(file, new Persona("Ana", 30));

      assertThat(JsonFiles.read(file, Persona.class)).isEqualTo(new Persona("Ana", 30));
    }
  }

  @Test
  @DisplayName("DownloadWaiter espera una descarga que termina más tarde")
  void waitsForDownload() {
    try (TempWorkspace workspace = TempWorkspace.create()) {
      CompletableFuture.runAsync(
          () -> {
            // Simula al navegador: primero el temporal, después el archivo final
            workspace.writeText("informe.pdf.crdownload", "parcial");
            workspace.writeText("informe.pdf", "completo");
          });

      Path file = DownloadWaiter.waitForFile(workspace.root(), "pdf", Duration.ofSeconds(5));

      assertThat(file.getFileName().toString()).isEqualTo("informe.pdf");
    }
  }

  @Test
  @DisplayName("DownloadWaiter falla con timeout si no aparece el archivo")
  void failsWhenNothingDownloads() {
    try (TempWorkspace workspace = TempWorkspace.create()) {
      assertThatThrownBy(
              () -> DownloadWaiter.waitForFile(workspace.root(), "pdf", Duration.ofMillis(500)))
          .isInstanceOf(ConditionTimeoutException.class);
    }
  }
}

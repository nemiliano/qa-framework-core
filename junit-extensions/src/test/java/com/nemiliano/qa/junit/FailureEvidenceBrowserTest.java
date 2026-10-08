package com.nemiliano.qa.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

/**
 * Ejecuta los escenarios que fallan a propósito y verifica que quedan las evidencias en disco.
 * Necesita navegadores: corre solo con {@code -DexcludedGroups=none}.
 */
@Tag("browser")
class FailureEvidenceBrowserTest {

  private static final Path EVIDENCE_DIR = Path.of(System.getProperty("evidence.dir"));

  @Test
  @DisplayName("Playwright: un fallo genera screenshot, HTML y trace.zip")
  void playwrightFailureLeavesEvidence() throws IOException {
    runExpectingOneFailure(FailingPlaywrightScenario.class);

    List<String> files = evidenceStartingWith("FailingPlaywrightScenario_failsOnPurpose");
    assertThat(files)
        .anyMatch(f -> f.endsWith("_screenshot.png"))
        .anyMatch(f -> f.endsWith("_page.html"))
        .anyMatch(f -> f.endsWith("_trace.zip"));
  }

  @Test
  @DisplayName("Selenium: un fallo genera screenshot, HTML y URL")
  void seleniumFailureLeavesEvidence() throws IOException {
    runExpectingOneFailure(FailingSeleniumScenario.class);

    List<String> files = evidenceStartingWith("FailingSeleniumScenario_failsOnPurpose");
    assertThat(files)
        .anyMatch(f -> f.endsWith("_screenshot.png"))
        .anyMatch(f -> f.endsWith("_page.html"))
        .anyMatch(f -> f.endsWith("_url.txt"));
  }

  private static void runExpectingOneFailure(Class<?> scenario) {
    EngineTestKit.engine("junit-jupiter")
        .selectors(selectClass(scenario))
        .execute()
        .testEvents()
        .assertStatistics(stats -> stats.started(1).failed(1));
  }

  private static List<String> evidenceStartingWith(String prefix) throws IOException {
    try (Stream<Path> stream = Files.list(EVIDENCE_DIR)) {
      return stream.map(p -> p.getFileName().toString()).filter(n -> n.startsWith(prefix)).toList();
    }
  }
}

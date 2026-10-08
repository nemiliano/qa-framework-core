package com.nemiliano.qa.junit;

import static org.assertj.core.api.Assertions.assertThat;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Escenario que falla A PROPÓSITO. No termina en "Test" para que Surefire no lo corra solo: lo
 * ejecuta {@link FailureEvidenceBrowserTest} para comprobar que se generan las evidencias.
 */
@Tag("browser")
@Tag("demo-failure")
@ExtendWith(PlaywrightExtension.class)
class FailingPlaywrightScenario {

  @Test
  @DisplayName("Caso: texto inexistente")
  void failsOnPurpose(Page page) {
    page.setContent("<h1>Hola</h1>");
    assertThat(page.locator("h1").textContent()).isEqualTo("Chau");
  }
}

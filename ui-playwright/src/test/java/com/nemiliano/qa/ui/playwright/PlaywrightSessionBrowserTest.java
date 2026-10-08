package com.nemiliano.qa.ui.playwright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.nemiliano.qa.core.config.ConfigLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Necesita los navegadores de Playwright: corre con {@code -DexcludedGroups=none}. */
@Tag("browser")
class PlaywrightSessionBrowserTest {

  private static final String PAGE =
      "<button id='go' onclick=\"setTimeout(()=>"
          + "document.getElementById('out').textContent='listo',300)\">Ir</button>"
          + "<p id='out'></p>";

  @Test
  @DisplayName("Auto-wait + aserción web-first: sin waits manuales")
  void autoWaitsForDynamicText() {
    try (PlaywrightSession session = PlaywrightSession.start(ConfigLoader.get())) {
      session.page().setContent(PAGE);
      session.page().click("#go");

      assertThat(session.page().locator("#out")).hasText("listo");
    }
  }
}

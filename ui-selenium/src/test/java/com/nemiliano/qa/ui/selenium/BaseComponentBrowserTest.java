package com.nemiliano.qa.ui.selenium;

import static org.assertj.core.api.Assertions.assertThat;

import com.nemiliano.qa.core.config.ConfigLoader;
import com.nemiliano.qa.core.config.QaConfig;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Necesita Chrome: corre con {@code -DexcludedGroups=none}. */
@Tag("browser")
class BaseComponentBrowserTest {

  // Página local (data URL): el texto aparece 300 ms después del click, sin depender de Internet
  private static final String PAGE =
      "data:text/html,<button id='go' onclick=\"setTimeout(()=>"
          + "document.getElementById('out').textContent='listo',300)\">Ir</button>"
          + "<p id='out'></p>";

  private WebDriver driver;
  private DemoPage pageObject;

  @BeforeEach
  void setUp() {
    QaConfig config = ConfigLoader.get();
    driver = DriverFactory.create(config);
    pageObject = new DemoPage(driver, Duration.ofSeconds(config.timeoutSeconds()));
  }

  @AfterEach
  void tearDown() {
    driver.quit();
  }

  @Test
  @DisplayName("waitTextNotEmpty espera al texto dinámico sin Thread.sleep")
  void waitsForDynamicText() {
    pageObject.open(PAGE);

    assertThat(pageObject.clickAndWaitResult()).isEqualTo("listo");
  }

  private static final class DemoPage extends BasePage {
    DemoPage(WebDriver driver, Duration timeout) {
      super(driver, timeout);
    }

    String clickAndWaitResult() {
      click(By.id("go"));
      return waitTextNotEmpty(By.id("out"));
    }
  }
}

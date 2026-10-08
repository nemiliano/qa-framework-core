package com.nemiliano.qa.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Igual que {@link FailingPlaywrightScenario}, pero con Selenium. */
@Tag("browser")
@Tag("demo-failure")
@ExtendWith(SeleniumExtension.class)
class FailingSeleniumScenario {

  @Test
  @DisplayName("Caso: texto inexistente")
  void failsOnPurpose(WebDriver driver) {
    driver.get("data:text/html,<h1>Hola</h1>");
    assertThat(driver.findElement(By.tagName("h1")).getText()).isEqualTo("Chau");
  }
}

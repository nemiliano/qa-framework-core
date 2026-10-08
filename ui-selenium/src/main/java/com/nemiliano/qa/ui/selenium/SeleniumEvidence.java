package com.nemiliano.qa.ui.selenium;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;

/** Obtiene las evidencias de una sesión Selenium. Cada método tolera fallos de captura. */
public final class SeleniumEvidence {

  private SeleniumEvidence() {}

  public static byte[] screenshot(WebDriver driver) {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
  }

  public static byte[] pageSource(WebDriver driver) {
    return driver.getPageSource().getBytes(StandardCharsets.UTF_8);
  }

  public static String currentUrl(WebDriver driver) {
    return driver.getCurrentUrl();
  }

  /** Logs de consola del navegador (solo Chrome/Edge). Vacío si el navegador no los expone. */
  public static String consoleLogs(WebDriver driver) {
    try {
      return driver.manage().logs().get("browser").getAll().stream()
          .filter(e -> e.getLevel().intValue() >= Level.INFO.intValue())
          .map(SeleniumEvidence::format)
          .collect(Collectors.joining("\n"));
    } catch (RuntimeException e) {
      return "";
    }
  }

  private static String format(LogEntry entry) {
    return entry.getLevel() + " " + entry.getMessage();
  }
}

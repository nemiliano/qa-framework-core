package com.nemiliano.qa.ui.selenium;

import com.nemiliano.qa.core.config.QaConfig;
import com.nemiliano.qa.core.exception.ConfigurationException;
import java.util.Locale;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Crea el {@link WebDriver}. Selenium Manager (incluido en Selenium 4) descarga y ubica el driver
 * correcto solo: no hay que bajar chromedriver a mano ni usar WebDriverManager.
 */
public final class DriverFactory {

  private DriverFactory() {}

  public static WebDriver create(QaConfig config) {
    String browser = config.browser().toLowerCase(Locale.ROOT);
    return switch (browser) {
      case "chromium", "chrome" -> new ChromeDriver(chromeOptions(config));
      case "edge" -> new EdgeDriver(edgeOptions(config));
      case "firefox" -> new FirefoxDriver(firefoxOptions(config));
      default ->
          throw new ConfigurationException(
              "Navegador no soportado por Selenium: '" + browser + "' (chrome, edge, firefox)");
    };
  }

  private static ChromeOptions chromeOptions(QaConfig config) {
    ChromeOptions options = new ChromeOptions();
    if (config.headless()) {
      options.addArguments("--headless=new");
    }
    options.addArguments("--window-size=1920,1080", "--disable-gpu", "--no-sandbox");
    return options;
  }

  private static EdgeOptions edgeOptions(QaConfig config) {
    EdgeOptions options = new EdgeOptions();
    if (config.headless()) {
      options.addArguments("--headless=new");
    }
    options.addArguments("--window-size=1920,1080");
    return options;
  }

  private static FirefoxOptions firefoxOptions(QaConfig config) {
    FirefoxOptions options = new FirefoxOptions();
    if (config.headless()) {
      options.addArguments("-headless");
    }
    options.addArguments("--width=1920", "--height=1080");
    return options;
  }
}

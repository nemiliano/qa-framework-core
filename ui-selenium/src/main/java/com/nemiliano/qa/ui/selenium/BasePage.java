package com.nemiliano.qa.ui.selenium;

import java.time.Duration;
import org.openqa.selenium.WebDriver;

/** Base de los Page Objects de Selenium. */
public abstract class BasePage extends BaseComponent {

  protected BasePage(WebDriver driver, Duration timeout) {
    super(driver, timeout);
  }

  public void open(String url) {
    driver.get(url);
  }

  public String currentUrl() {
    return driver.getCurrentUrl();
  }

  public String title() {
    return driver.getTitle();
  }
}

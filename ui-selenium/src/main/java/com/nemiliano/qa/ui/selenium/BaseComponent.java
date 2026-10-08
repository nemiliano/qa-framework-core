package com.nemiliano.qa.ui.selenium;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Waits explícitos semánticos. Nunca se usa {@code Thread.sleep} ni implicit wait: cada acción
 * espera exactamente la condición que necesita.
 */
public class BaseComponent {

  protected final WebDriver driver;
  protected final WebDriverWait wait;

  public BaseComponent(WebDriver driver, Duration timeout) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, timeout);
    this.wait.ignoring(StaleElementReferenceException.class);
  }

  /** Espera a que el elemento sea visible y habilitado, y lo devuelve. */
  protected WebElement clickable(By by) {
    return wait.until(ExpectedConditions.elementToBeClickable(by));
  }

  /** Espera a que el elemento sea visible y lo devuelve. */
  protected WebElement visible(By by) {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  /** Espera a que el elemento tenga texto no vacío y lo devuelve. */
  protected String waitTextNotEmpty(By by) {
    return wait.until(
        d -> {
          String text = d.findElement(by).getText();
          return text == null || text.isBlank() ? null : text;
        });
  }

  /** Texto de un elemento visible. */
  protected String getTextElement(By by) {
    return visible(by).getText();
  }

  protected void click(By by) {
    clickable(by).click();
  }

  /**
   * Hace click en {@code target} y espera a que aparezca {@code expected}; si no aparece, vuelve a
   * hacer click mientras {@code target} siga presente. Resuelve el caso típico de una página que
   * muestra el botón antes de enganchar su manejador: el primer click se pierde sin error.
   *
   * <p>Usar solo cuando el click hace desaparecer o reemplazar a {@code target} (por ejemplo "Add
   * to cart" pasa a "Remove"); si no, un reintento podría repetir la acción.
   */
  protected void clickUntilVisible(By target, By expected) {
    wait.until(
        d -> {
          if (!d.findElements(expected).isEmpty() && d.findElement(expected).isDisplayed()) {
            return true;
          }
          d.findElements(target).stream().findFirst().ifPresent(WebElement::click);
          return false;
        });
  }

  protected void type(By by, String text) {
    WebElement element = visible(by);
    element.clear();
    element.sendKeys(text);
  }
}

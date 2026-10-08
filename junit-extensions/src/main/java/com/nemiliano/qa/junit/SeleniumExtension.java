package com.nemiliano.qa.junit;

import com.nemiliano.qa.core.config.ConfigLoader;
import com.nemiliano.qa.reporting.Evidence;
import com.nemiliano.qa.reporting.EvidenceNamer;
import com.nemiliano.qa.ui.selenium.DriverFactory;
import com.nemiliano.qa.ui.selenium.SeleniumEvidence;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entrega un {@link WebDriver} nuevo a cada test (parámetro del método), lo cierra al terminar y,
 * si el test falla, guarda y adjunta a Allure: screenshot, HTML, URL y logs de consola.
 */
public class SeleniumExtension
    implements ParameterResolver, TestExecutionExceptionHandler, AfterEachCallback {

  private static final Logger LOG = LoggerFactory.getLogger(SeleniumExtension.class);
  private static final Namespace NS = Namespace.create(SeleniumExtension.class);

  @Override
  public boolean supportsParameter(ParameterContext parameter, ExtensionContext context) {
    return parameter.getParameter().getType() == WebDriver.class;
  }

  @Override
  public Object resolveParameter(ParameterContext parameter, ExtensionContext context) {
    return context
        .getStore(NS)
        .getOrComputeIfAbsent(
            WebDriver.class, k -> DriverFactory.create(ConfigLoader.get()), WebDriver.class);
  }

  /** Se ejecuta cuando el test lanza una excepción, con el navegador todavía abierto. */
  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    WebDriver driver = context.getStore(NS).get(WebDriver.class, WebDriver.class);
    if (driver != null) {
      captureEvidence(driver, context);
    }
    throw throwable; // el test sigue marcado como fallido
  }

  @Override
  public void afterEach(ExtensionContext context) {
    WebDriver driver = context.getStore(NS).remove(WebDriver.class, WebDriver.class);
    if (driver != null) {
      driver.quit();
    }
  }

  private static void captureEvidence(WebDriver driver, ExtensionContext context) {
    Path dir = Path.of(ConfigLoader.get().evidenceDir());
    String base =
        EvidenceNamer.baseName(
            context.getRequiredTestClass().getSimpleName(),
            context.getRequiredTestMethod().getName(),
            context.getDisplayName());
    try {
      Evidence.saveAndAttach(
          dir,
          base,
          "screenshot",
          "Screenshot",
          "image/png",
          "png",
          SeleniumEvidence.screenshot(driver));
      Evidence.saveAndAttach(
          dir,
          base,
          "page",
          "HTML de la página",
          "text/html",
          "html",
          SeleniumEvidence.pageSource(driver));
      Evidence.saveAndAttach(
          dir,
          base,
          "url",
          "URL actual",
          "text/plain",
          "txt",
          SeleniumEvidence.currentUrl(driver).getBytes(StandardCharsets.UTF_8));
      String console = SeleniumEvidence.consoleLogs(driver);
      if (!console.isBlank()) {
        Evidence.saveAndAttach(
            dir,
            base,
            "console",
            "Logs de consola",
            "text/plain",
            "txt",
            console.getBytes(StandardCharsets.UTF_8));
      }
    } catch (RuntimeException e) {
      // La evidencia nunca debe ocultar el error real del test
      LOG.warn("No se pudo capturar la evidencia de {}: {}", base, e.toString());
    }
  }
}

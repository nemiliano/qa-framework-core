package com.nemiliano.qa.junit;

import com.microsoft.playwright.Page;
import com.nemiliano.qa.core.config.ConfigLoader;
import com.nemiliano.qa.reporting.Evidence;
import com.nemiliano.qa.reporting.EvidenceNamer;
import com.nemiliano.qa.ui.playwright.PlaywrightSession;
import java.nio.file.Path;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entrega una {@link Page} (o la {@link PlaywrightSession} completa) nueva a cada test, con su
 * propio BrowserContext aislado. Ante un fallo guarda y adjunta a Allure: screenshot de página
 * completa, HTML y el {@code trace.zip}. Si el test pasa, el trace se descarta.
 */
public class PlaywrightExtension
    implements ParameterResolver, TestExecutionExceptionHandler, AfterEachCallback {

  private static final Logger LOG = LoggerFactory.getLogger(PlaywrightExtension.class);
  private static final Namespace NS = Namespace.create(PlaywrightExtension.class);

  @Override
  public boolean supportsParameter(ParameterContext parameter, ExtensionContext context) {
    Class<?> type = parameter.getParameter().getType();
    return type == Page.class || type == PlaywrightSession.class;
  }

  @Override
  public Object resolveParameter(ParameterContext parameter, ExtensionContext context) {
    PlaywrightSession session =
        context
            .getStore(NS)
            .getOrComputeIfAbsent(
                PlaywrightSession.class,
                k -> PlaywrightSession.start(ConfigLoader.get()),
                PlaywrightSession.class);
    return parameter.getParameter().getType() == Page.class ? session.page() : session;
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    PlaywrightSession session =
        context.getStore(NS).get(PlaywrightSession.class, PlaywrightSession.class);
    if (session != null) {
      captureEvidence(session, context);
    }
    throw throwable;
  }

  @Override
  public void afterEach(ExtensionContext context) {
    PlaywrightSession session =
        context.getStore(NS).remove(PlaywrightSession.class, PlaywrightSession.class);
    if (session != null) {
      session.close();
    }
  }

  private static void captureEvidence(PlaywrightSession session, ExtensionContext context) {
    Path dir = Path.of(ConfigLoader.get().evidenceDir());
    String base =
        EvidenceNamer.baseName(
            context.getRequiredTestClass().getSimpleName(),
            context.getRequiredTestMethod().getName(),
            context.getDisplayName());
    try {
      Evidence.saveAndAttach(
          dir, base, "screenshot", "Screenshot", "image/png", "png", session.screenshot());
      Evidence.saveAndAttach(
          dir, base, "page", "HTML de la página", "text/html", "html", session.html());
      Path trace = session.saveTrace(dir.resolve(base + "_trace.zip"));
      Evidence.attachFile("Playwright trace", "application/zip", trace);
    } catch (RuntimeException e) {
      LOG.warn("No se pudo capturar la evidencia de {}: {}", base, e.toString());
    }
  }
}

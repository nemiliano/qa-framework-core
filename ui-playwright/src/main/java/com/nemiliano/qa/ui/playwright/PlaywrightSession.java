package com.nemiliano.qa.ui.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.nemiliano.qa.core.config.QaConfig;
import com.nemiliano.qa.core.exception.ConfigurationException;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Una sesión Playwright por test: Playwright + Browser + BrowserContext + Page. Los objetos de
 * Playwright no son thread-safe, así que nunca se comparten entre tests (ADR-0003).
 *
 * <p>El tracing queda activo todo el test, pero el {@code trace.zip} solo se escribe en disco si se
 * llama a {@link #saveTrace(Path)} (la extensión lo hace únicamente ante un fallo).
 */
public final class PlaywrightSession implements AutoCloseable {

  private final Playwright playwright;
  private final Browser browser;
  private final BrowserContext context;
  private final Page page;

  private PlaywrightSession(
      Playwright playwright, Browser browser, BrowserContext context, Page page) {
    this.playwright = playwright;
    this.browser = browser;
    this.context = context;
    this.page = page;
  }

  public static PlaywrightSession start(QaConfig config) {
    Playwright playwright = Playwright.create();
    try {
      BrowserType.LaunchOptions launch =
          new BrowserType.LaunchOptions()
              .setHeadless(config.headless())
              .setSlowMo(config.slowMoMs());
      Browser browser = browserType(playwright, config.browser()).launch(launch);

      // Un contexto nuevo por test = cookies, storage y sesión totalmente aislados
      BrowserContext context =
          browser.newContext(new Browser.NewContextOptions().setViewportSize(1920, 1080));
      context.setDefaultTimeout(config.timeoutSeconds() * 1000.0);
      context
          .tracing()
          .start(
              new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
      return new PlaywrightSession(playwright, browser, context, context.newPage());
    } catch (RuntimeException e) {
      playwright.close();
      throw e;
    }
  }

  private static BrowserType browserType(Playwright playwright, String name) {
    return switch (name.toLowerCase(Locale.ROOT)) {
      case "chromium", "chrome" -> playwright.chromium();
      case "firefox" -> playwright.firefox();
      case "webkit" -> playwright.webkit();
      default ->
          throw new ConfigurationException(
              "Navegador no soportado por Playwright: '" + name + "' (chromium, firefox, webkit)");
    };
  }

  public Page page() {
    return page;
  }

  public BrowserContext context() {
    return context;
  }

  public byte[] screenshot() {
    return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
  }

  public byte[] html() {
    return page.content().getBytes(java.nio.charset.StandardCharsets.UTF_8);
  }

  /** Detiene el tracing y escribe el trace.zip. Se abre con {@code playwright show-trace}. */
  public Path saveTrace(Path file) {
    context.tracing().stop(new Tracing.StopOptions().setPath(file));
    return file;
  }

  @Override
  public void close() {
    try {
      context.close();
      browser.close();
    } finally {
      playwright.close();
    }
  }
}

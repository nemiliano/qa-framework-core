package com.nemiliano.qa.ui.playwright;

import com.microsoft.playwright.Page;

/**
 * Base de los Page Objects de Playwright. Es mínima a propósito: Playwright ya espera solo
 * (auto-wait), así que no hacen falta métodos como {@code clickable()} o {@code visible()}.
 */
public abstract class BasePage {

  protected final Page page;

  protected BasePage(Page page) {
    this.page = page;
  }

  public void open(String url) {
    page.navigate(url);
  }

  public String title() {
    return page.title();
  }

  public String currentUrl() {
    return page.url();
  }
}

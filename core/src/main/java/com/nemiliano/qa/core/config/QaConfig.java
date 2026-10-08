package com.nemiliano.qa.core.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Key;

/**
 * Configuración tipada del framework. Prioridad de valores (mayor a menor):
 *
 * <ol>
 *   <li>Variable de ambiente (clave en MAYÚSCULAS con guion bajo en lugar de punto, ej. {@code
 *       BASE_URL})
 *   <li>Propiedad de sistema ({@code -Dbase.url=...})
 *   <li>Archivo {@code config/<ambiente>.properties}
 *   <li>Valor por defecto declarado aquí
 * </ol>
 */
public interface QaConfig extends Config {

  @Key("qa.env")
  @DefaultValue("local")
  String environment();

  @Key("base.url")
  @DefaultValue("https://www.saucedemo.com")
  String baseUrl();

  @Key("ui.engine")
  @DefaultValue("playwright")
  String uiEngine();

  @Key("browser")
  @DefaultValue("chromium")
  String browser();

  @Key("headless")
  @DefaultValue("true")
  boolean headless();

  @Key("timeout.seconds")
  @DefaultValue("10")
  int timeoutSeconds();
}

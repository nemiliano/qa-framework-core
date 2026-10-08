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

  /** Pausa entre acciones de Playwright en milisegundos (solo para depurar a ojo). */
  @Key("slowmo.ms")
  @DefaultValue("0")
  int slowMoMs();

  @Key("api.base.url")
  @DefaultValue("https://restful-booker.herokuapp.com")
  String apiBaseUrl();

  /** URL JDBC. El driver del motor (Oracle, PostgreSQL...) lo agrega el proyecto consumidor. */
  @Key("db.url")
  @DefaultValue("jdbc:h2:mem:qa;DB_CLOSE_DELAY=-1")
  String dbUrl();

  @Key("db.user")
  @DefaultValue("sa")
  String dbUser();

  /** Nunca va en un archivo del repo: usar la variable de ambiente DB_PASSWORD. */
  @Key("db.password")
  @DefaultValue("")
  String dbPassword();

  /** Carpeta donde se guardan screenshots, HTML y trace ante un fallo. */
  @Key("evidence.dir")
  @DefaultValue("target/evidence")
  String evidenceDir();
}

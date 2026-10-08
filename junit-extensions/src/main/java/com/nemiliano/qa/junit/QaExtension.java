package com.nemiliano.qa.junit;

import com.nemiliano.qa.core.config.ConfigLoader;
import com.nemiliano.qa.core.config.QaConfig;
import java.util.Optional;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extensión base del framework. Se usa con {@code @ExtendWith(QaExtension.class)} y reemplaza al
 * {@code BaseTest}: en lugar de heredar, se compone.
 *
 * <ul>
 *   <li>Loguea inicio y resultado de cada test.
 *   <li>Inyecta {@link QaConfig} como parámetro de métodos y constructores.
 * </ul>
 *
 * Fases siguientes: driver/page por parámetro y evidencias ante fallo.
 */
public class QaExtension implements BeforeEachCallback, TestWatcher, ParameterResolver {

  private static final Logger LOG = LoggerFactory.getLogger(QaExtension.class);

  @Override
  public void beforeEach(ExtensionContext context) {
    LOG.info("INICIO   {}", context.getDisplayName());
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    LOG.info("OK       {}", context.getDisplayName());
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    LOG.error("FALLO    {} -> {}", context.getDisplayName(), cause.toString());
  }

  @Override
  public void testAborted(ExtensionContext context, Throwable cause) {
    LOG.warn("ABORTADO {}", context.getDisplayName());
  }

  @Override
  public void testDisabled(ExtensionContext context, Optional<String> reason) {
    LOG.info("OMITIDO  {} ({})", context.getDisplayName(), reason.orElse("sin motivo"));
  }

  @Override
  public boolean supportsParameter(ParameterContext parameter, ExtensionContext context) {
    return parameter.getParameter().getType() == QaConfig.class;
  }

  @Override
  public Object resolveParameter(ParameterContext parameter, ExtensionContext context) {
    return ConfigLoader.get();
  }
}

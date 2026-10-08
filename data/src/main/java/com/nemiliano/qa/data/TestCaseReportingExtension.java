package com.nemiliano.qa.data;

import io.qameta.allure.Allure;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Publica en Allure y en el log el "Caso de prueba" y los datos de entrada de cada iteración. Se
 * activa solo al usar {@link CsvTestData} o {@link JsonTestData}.
 */
public class TestCaseReportingExtension implements InvocationInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(TestCaseReportingExtension.class);

  private static final Map<String, String> LABELS =
      Map.of("casoDePrueba", "Caso de prueba", "resultadoEsperado", "Resultado esperado");

  @Override
  public void interceptTestTemplateMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext)
      throws Throwable {
    for (Object argument : invocationContext.getArguments()) {
      if (argument instanceof TestCase testCase) {
        report(testCase);
      }
    }
    invocation.proceed();
  }

  private static void report(TestCase testCase) throws ReflectiveOperationException {
    LOG.info("Caso de prueba: {}", testCase.casoDePrueba());
    for (RecordComponent component : testCase.getClass().getRecordComponents()) {
      component.getAccessor().setAccessible(true);
      Object value = component.getAccessor().invoke(testCase);
      String label = LABELS.getOrDefault(component.getName(), component.getName());
      Allure.parameter(label, isSensitive(component.getName()) ? "***" : value);
    }
  }

  /** Las claves y tokens nunca deben llegar al reporte. */
  static boolean isSensitive(String name) {
    String lower = name.toLowerCase(Locale.ROOT);
    return lower.contains("password")
        || lower.contains("clave")
        || lower.contains("secret")
        || lower.contains("token");
  }
}

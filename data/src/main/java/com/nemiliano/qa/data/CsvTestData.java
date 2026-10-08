package com.nemiliano.qa.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * Alimenta un {@code @ParameterizedTest} con las filas de un CSV. Cada fila llega como un record
 * tipado y el test se muestra con el texto de su "Caso de prueba".
 *
 * <pre>{@code
 * @ParameterizedTest(name = "{0}")
 * @CsvTestData(file = "data/login.csv", type = LoginData.class)
 * void login(LoginData data) { ... }
 * }</pre>
 */
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(CsvTestDataProvider.class)
@ExtendWith(TestCaseReportingExtension.class)
public @interface CsvTestData {

  /** Ruta del archivo dentro del classpath (por ejemplo {@code data/login.csv}). */
  String file();

  /** Record que modela una fila; debe implementar {@link TestCase}. */
  Class<? extends TestCase> type();
}

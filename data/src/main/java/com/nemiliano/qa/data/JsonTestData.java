package com.nemiliano.qa.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.ArgumentsSource;

/** Igual que {@link CsvTestData} pero para un JSON con un arreglo de objetos. */
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(JsonTestDataProvider.class)
@ExtendWith(TestCaseReportingExtension.class)
public @interface JsonTestData {

  String file();

  Class<? extends TestCase> type();
}

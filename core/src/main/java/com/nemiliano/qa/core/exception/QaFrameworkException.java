package com.nemiliano.qa.core.exception;

/** Excepción base del framework. Evita propagar excepciones genéricas o de terceros. */
public class QaFrameworkException extends RuntimeException {

  public QaFrameworkException(String message) {
    super(message);
  }

  public QaFrameworkException(String message, Throwable cause) {
    super(message, cause);
  }
}

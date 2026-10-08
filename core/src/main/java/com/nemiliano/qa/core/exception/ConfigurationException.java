package com.nemiliano.qa.core.exception;

/** Configuración inexistente o inválida. */
public class ConfigurationException extends QaFrameworkException {

  public ConfigurationException(String message) {
    super(message);
  }

  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}

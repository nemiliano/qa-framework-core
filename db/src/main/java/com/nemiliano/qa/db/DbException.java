package com.nemiliano.qa.db;

import com.nemiliano.qa.core.exception.QaFrameworkException;

/**
 * Error de base de datos. El mensaje incluye el SQL pero nunca los parámetros (pueden ser datos
 * sensibles).
 */
public class DbException extends QaFrameworkException {

  public DbException(String message, Throwable cause) {
    super(message, cause);
  }
}

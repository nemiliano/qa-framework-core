package com.nemiliano.qa.data;

import com.nemiliano.qa.core.exception.QaFrameworkException;

/** Archivo de datos inexistente o inválido. El mensaje indica archivo, fila y columna. */
public class DataException extends QaFrameworkException {

  public DataException(String message) {
    super(message);
  }

  public DataException(String message, Throwable cause) {
    super(message, cause);
  }
}

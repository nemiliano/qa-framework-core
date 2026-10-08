package com.nemiliano.qa.api;

import io.restassured.response.Response;

/** Respuesta HTTP sin exponer los tipos de REST Assured (se puede cambiar de cliente, ADR-0004). */
public final class ApiResponse {

  private final Response response;

  ApiResponse(Response response) {
    this.response = response;
  }

  public int status() {
    return response.statusCode();
  }

  public String body() {
    return response.asString();
  }

  public String header(String name) {
    return response.getHeader(name);
  }

  /** Valor de una ruta JsonPath/GPath, por ejemplo {@code "booking.firstname"}. */
  public <T> T jsonPath(String path) {
    return response.jsonPath().get(path);
  }

  /** Convierte el JSON de la respuesta a un objeto. */
  public <T> T as(Class<T> type) {
    return response.as(type);
  }

  public long timeMillis() {
    return response.time();
  }
}

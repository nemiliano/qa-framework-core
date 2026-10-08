package com.nemiliano.qa.api;

import com.nemiliano.qa.core.config.QaConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

/**
 * Cliente HTTP genérico. Cada request/response queda adjunto al paso de Allure. Es inmutable: los
 * métodos {@code with...} devuelven un cliente nuevo, así que se puede compartir entre hilos.
 */
public final class ApiClient {

  private final RequestSpecBuilder base;

  private ApiClient(RequestSpecBuilder base) {
    this.base = base;
  }

  public static ApiClient create(String baseUrl) {
    RequestSpecBuilder builder =
        new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .setContentType(ContentType.JSON)
            // Texto fijo: ContentType.JSON enviaría una lista de 4 tipos y algunos servidores
            // estrictos (por ejemplo restful-booker) responden 418
            .setAccept("application/json")
            .addFilter(new AllureRestAssured());
    return new ApiClient(builder);
  }

  public static ApiClient fromConfig(QaConfig config) {
    return create(config.apiBaseUrl());
  }

  /** Cliente nuevo con un header adicional (por ejemplo un token). */
  public ApiClient withHeader(String name, String value) {
    return new ApiClient(copy().addHeader(name, value));
  }

  public ApiResponse get(String path) {
    return new ApiResponse(spec().get(path));
  }

  public ApiResponse get(String path, Map<String, ?> queryParams) {
    return new ApiResponse(spec().queryParams(queryParams).get(path));
  }

  public ApiResponse post(String path, Object body) {
    return new ApiResponse(spec().body(body).post(path));
  }

  public ApiResponse put(String path, Object body) {
    return new ApiResponse(spec().body(body).put(path));
  }

  public ApiResponse delete(String path) {
    return new ApiResponse(spec().delete(path));
  }

  private RequestSpecification spec() {
    return RestAssured.given().spec(base.build());
  }

  private RequestSpecBuilder copy() {
    return new RequestSpecBuilder().addRequestSpecification(base.build());
  }
}

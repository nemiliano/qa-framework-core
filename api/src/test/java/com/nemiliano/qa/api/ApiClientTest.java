package com.nemiliano.qa.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Prueba el cliente contra un servidor HTTP local: sin Internet y sin flakiness. */
class ApiClientTest {

  private static HttpServer server;
  private static ApiClient client;

  @BeforeAll
  static void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext(
        "/ping", ex -> reply(ex, 200, "{\"mensaje\":\"pong\",\"nivel\":{\"valor\":7}}"));
    server.createContext(
        "/echo",
        ex ->
            reply(ex, 201, new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)));
    server.createContext(
        "/auth",
        ex -> reply(ex, 200, "{\"token\":\"" + ex.getRequestHeaders().getFirst("X-Token") + "\"}"));
    server.start();
    client = ApiClient.create("http://127.0.0.1:" + server.getAddress().getPort());
  }

  @AfterAll
  static void stopServer() {
    server.stop(0);
  }

  @Test
  @DisplayName("GET devuelve status y permite leer el JSON por ruta")
  void getReadsJsonPath() {
    ApiResponse response = client.get("/ping");

    assertThat(response.status()).isEqualTo(200);
    assertThat(response.<String>jsonPath("mensaje")).isEqualTo("pong");
    assertThat(response.<Integer>jsonPath("nivel.valor")).isEqualTo(7);
  }

  @Test
  @DisplayName("POST envía el body como JSON")
  void postSendsJsonBody() {
    ApiResponse response = client.post("/echo", Map.of("nombre", "Ana"));

    assertThat(response.status()).isEqualTo(201);
    assertThat(response.<String>jsonPath("nombre")).isEqualTo("Ana");
  }

  @Test
  @DisplayName("withHeader crea un cliente nuevo sin modificar el original")
  void withHeaderIsImmutable() {
    ApiClient authenticated = client.withHeader("X-Token", "abc");

    assertThat(authenticated.get("/auth").<String>jsonPath("token")).isEqualTo("abc");
    assertThat(client.get("/auth").<String>jsonPath("token")).isEqualTo("null");
  }

  @Test
  @DisplayName("Una ruta inexistente devuelve 404")
  void unknownPathReturns404() {
    assertThat(client.get("/no-existe").status()).isEqualTo(404);
  }

  private static void reply(HttpExchange exchange, int status, String body) throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(status, bytes.length);
    exchange.getResponseBody().write(bytes);
    exchange.close();
  }
}

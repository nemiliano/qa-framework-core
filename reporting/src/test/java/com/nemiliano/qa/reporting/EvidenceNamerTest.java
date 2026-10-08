package com.nemiliano.qa.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EvidenceNamerTest {

  @Test
  @DisplayName("Une clase, método y caso de prueba")
  void joinsParts() {
    assertThat(EvidenceNamer.baseName("LoginTest", "validUser", "Usuario válido"))
        .isEqualTo("LoginTest_validUser_Usuario_válido");
  }

  @Test
  @DisplayName("Reemplaza caracteres inválidos para archivos")
  void sanitizes() {
    assertThat(EvidenceNamer.baseName("A", "b", "caso: 1/2 <x>?")).isEqualTo("A_b_caso_1_2_x");
  }

  @Test
  @DisplayName("Limita la longitud")
  void truncates() {
    assertThat(EvidenceNamer.baseName("A", "b", "x".repeat(500))).hasSize(120);
  }
}

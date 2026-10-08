package com.nemiliano.qa.junit;

import static org.assertj.core.api.Assertions.assertThat;

import com.nemiliano.qa.core.config.QaConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(QaExtension.class)
class QaExtensionTest {

  @Test
  @DisplayName("La extensión inyecta QaConfig como parámetro")
  void injectsConfig(QaConfig config) {
    assertThat(config).isNotNull();
    assertThat(config.baseUrl()).startsWith("https://");
  }
}

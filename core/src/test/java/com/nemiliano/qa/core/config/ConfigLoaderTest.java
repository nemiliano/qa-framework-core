package com.nemiliano.qa.core.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nemiliano.qa.core.exception.ConfigurationException;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfigLoaderTest {

  @Test
  @DisplayName("Sin overrides usa el archivo del ambiente local")
  void usesFileValues() {
    QaConfig config = ConfigLoader.load(Map.of(), new Properties());

    assertThat(config.environment()).isEqualTo("local");
    assertThat(config.headless()).isFalse(); // viene de local.properties
    assertThat(config.timeoutSeconds()).isEqualTo(10); // valor por defecto
  }

  @Test
  @DisplayName("La propiedad de sistema pisa al archivo")
  void systemPropertyOverridesFile() {
    Properties sys = new Properties();
    sys.setProperty("headless", "true");

    assertThat(ConfigLoader.load(Map.of(), sys).headless()).isTrue();
  }

  @Test
  @DisplayName("La variable de ambiente pisa a la propiedad de sistema")
  void envVarOverridesSystemProperty() {
    Properties sys = new Properties();
    sys.setProperty("timeout.seconds", "20");

    QaConfig config = ConfigLoader.load(Map.of("TIMEOUT_SECONDS", "30"), sys);

    assertThat(config.timeoutSeconds()).isEqualTo(30);
  }

  @Test
  @DisplayName("QA_ENV selecciona el archivo del ambiente")
  void environmentSelectsFile() {
    QaConfig config = ConfigLoader.load(Map.of("QA_ENV", "qa"), new Properties());

    assertThat(config.environment()).isEqualTo("qa");
    assertThat(config.headless()).isTrue(); // viene de qa.properties
  }

  @Test
  @DisplayName("Un ambiente sin archivo falla con mensaje claro")
  void unknownEnvironmentFails() {
    assertThatThrownBy(() -> ConfigLoader.load(Map.of("QA_ENV", "inexistente"), new Properties()))
        .isInstanceOf(ConfigurationException.class)
        .hasMessageContaining("config/inexistente.properties");
  }
}

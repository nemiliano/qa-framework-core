package com.nemiliano.qa.core.config;

import com.nemiliano.qa.core.exception.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.aeonbits.owner.Config.Key;
import org.aeonbits.owner.ConfigFactory;

/** Arma {@link QaConfig} combinando archivo, propiedades de sistema y variables de ambiente. */
public final class ConfigLoader {

  private static final String ENV_KEY = "qa.env";
  private static final String DEFAULT_ENV = "local";
  private static volatile QaConfig instance;

  private ConfigLoader() {}

  /** Configuración compartida del proceso (se calcula una sola vez). */
  public static QaConfig get() {
    if (instance == null) {
      synchronized (ConfigLoader.class) {
        if (instance == null) {
          instance = load(System.getenv(), System.getProperties());
        }
      }
    }
    return instance;
  }

  /** Versión testeable: recibe las fuentes en lugar de leerlas del proceso. */
  static QaConfig load(Map<String, String> envVars, Properties sysProps) {
    String environment = resolveEnvironment(envVars, sysProps);

    Map<String, String> merged = new HashMap<>();
    loadFile(environment).forEach((k, v) -> merged.put(k.toString(), v.toString()));
    for (String key : declaredKeys()) {
      String fromSystem = sysProps.getProperty(key);
      if (fromSystem != null) {
        merged.put(key, fromSystem);
      }
      String fromEnv = envVars.get(toEnvName(key));
      if (fromEnv != null) {
        merged.put(key, fromEnv);
      }
    }
    merged.put(ENV_KEY, environment);
    return ConfigFactory.create(QaConfig.class, merged);
  }

  static String toEnvName(String key) {
    return key.toUpperCase().replace('.', '_');
  }

  private static String resolveEnvironment(Map<String, String> envVars, Properties sysProps) {
    String fromEnv = envVars.get(toEnvName(ENV_KEY));
    if (fromEnv != null) {
      return fromEnv;
    }
    return sysProps.getProperty(ENV_KEY, "local");
  }

  private static Properties loadFile(String environment) {
    Properties props = new Properties();
    String resource = "config/" + environment + ".properties";
    try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream(resource)) {
      if (in == null) {
        if (DEFAULT_ENV.equals(environment)) {
          return props; // el ambiente por defecto puede funcionar solo con los valores por defecto
        }
        throw new ConfigurationException(
            "No existe el archivo de configuración '"
                + resource
                + "' para el ambiente '"
                + environment
                + "'");
      }
      props.load(in);
    } catch (IOException e) {
      throw new ConfigurationException("No se pudo leer " + resource, e);
    }
    return props;
  }

  private static List<String> declaredKeys() {
    List<String> keys = new ArrayList<>();
    for (Method m : QaConfig.class.getMethods()) {
      Key key = m.getAnnotation(Key.class);
      if (key != null) {
        keys.add(key.value());
      }
    }
    return keys;
  }
}

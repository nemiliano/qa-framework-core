package com.nemiliano.qa.data;

import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.support.ParameterDeclarations;

/** Convierte los elementos del JSON en argumentos; el nombre visible es el "Caso de prueba". */
public class JsonTestDataProvider extends AnnotationBasedArgumentsProvider<JsonTestData> {

  @Override
  protected Stream<? extends Arguments> provideArguments(
      ParameterDeclarations parameters, ExtensionContext context, JsonTestData annotation) {
    return JsonDataLoader.load(annotation.file(), annotation.type()).stream()
        .map(row -> Arguments.of(Named.of(row.casoDePrueba(), row)));
  }
}

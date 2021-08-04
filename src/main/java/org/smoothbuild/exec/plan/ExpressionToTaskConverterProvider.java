package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.Definitions;

public class ExpressionToTaskConverterProvider {
  private final TypeToSpecConverter typeToSpecConverter;
  private final MethodLoader methodLoader;

  @Inject
  public ExpressionToTaskConverterProvider(TypeToSpecConverter typeToSpecConverter,
      MethodLoader methodLoader) {
    this.typeToSpecConverter = typeToSpecConverter;
    this.methodLoader = methodLoader;
  }

  public ExpressionToTaskConverter get(Definitions definitions) {
    return new ExpressionToTaskConverter(definitions, typeToSpecConverter, methodLoader);
  }
}

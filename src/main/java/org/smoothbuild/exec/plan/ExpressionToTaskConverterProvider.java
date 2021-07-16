package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.Definitions;

public class ExpressionToTaskConverterProvider {
  private final ObjectFactory objectFactory;
  private final MethodLoader methodLoader;

  @Inject
  public ExpressionToTaskConverterProvider(ObjectFactory objectFactory,
      MethodLoader methodLoader) {
    this.objectFactory = objectFactory;
    this.methodLoader = methodLoader;
  }

  public ExpressionToTaskConverter get(Definitions definitions) {
    return new ExpressionToTaskConverter(definitions, objectFactory, methodLoader);
  }
}

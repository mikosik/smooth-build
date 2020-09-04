package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.lang.parse.Definitions;

public class ExpressionToTaskConverterProvider {
  private final ObjectFactory objectFactory;

  @Inject
  public ExpressionToTaskConverterProvider(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  public ExpressionToTaskConverter get(Definitions definitions) {
    return new ExpressionToTaskConverter(definitions, objectFactory);
  }
}

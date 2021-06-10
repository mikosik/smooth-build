package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.exec.java.JavaCodeLoader;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.lang.base.define.Definitions;

public class ExpressionToTaskConverterProvider {
  private final ObjectFactory objectFactory;
  private final JavaCodeLoader javaCodeLoader;
  private final FullPathResolver fullPathResolver;

  @Inject
  public ExpressionToTaskConverterProvider(ObjectFactory objectFactory,
      JavaCodeLoader javaCodeLoader, FullPathResolver fullPathResolver) {
    this.objectFactory = objectFactory;
    this.javaCodeLoader = javaCodeLoader;
    this.fullPathResolver = fullPathResolver;
  }

  public ExpressionToTaskConverter get(Definitions definitions) {
    return new ExpressionToTaskConverter(
        definitions, objectFactory, javaCodeLoader, fullPathResolver);
  }
}

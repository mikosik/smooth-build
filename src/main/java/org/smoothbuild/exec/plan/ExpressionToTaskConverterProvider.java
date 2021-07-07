package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.exec.java.JavaCodeLoader;
import org.smoothbuild.io.fs.base.FileResolver;
import org.smoothbuild.lang.base.define.Definitions;

public class ExpressionToTaskConverterProvider {
  private final ObjectFactory objectFactory;
  private final JavaCodeLoader javaCodeLoader;
  private final FileResolver fileResolver;

  @Inject
  public ExpressionToTaskConverterProvider(ObjectFactory objectFactory,
      JavaCodeLoader javaCodeLoader, FileResolver fileResolver) {
    this.objectFactory = objectFactory;
    this.javaCodeLoader = javaCodeLoader;
    this.fileResolver = fileResolver;
  }

  public ExpressionToTaskConverter get(Definitions definitions) {
    return new ExpressionToTaskConverter(
        definitions, objectFactory, javaCodeLoader, fileResolver);
  }
}

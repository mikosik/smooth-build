package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.exec.nativ.NativeImplLoader;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.lang.base.define.Definitions;

public class ExpressionToTaskConverterProvider {
  private final ObjectFactory objectFactory;
  private final NativeImplLoader nativeImplLoader;
  private final FullPathResolver pathResolver;

  @Inject
  public ExpressionToTaskConverterProvider(ObjectFactory objectFactory,
      NativeImplLoader nativeImplLoader, FullPathResolver pathResolver) {
    this.objectFactory = objectFactory;
    this.nativeImplLoader = nativeImplLoader;
    this.pathResolver = pathResolver;
  }

  public ExpressionToTaskConverter get(Definitions definitions) {
    return new ExpressionToTaskConverter(
        definitions, objectFactory, nativeImplLoader, pathResolver);
  }
}

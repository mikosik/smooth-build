package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.exec.nativ.NativeImplLoader;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.lang.base.define.Definitions;

public class ExpressionToTaskConverterProvider {
  private final ObjectFactory objectFactory;
  private final NativeImplLoader nativeImplLoader;
  private final FullPathResolver fullPathResolver;

  @Inject
  public ExpressionToTaskConverterProvider(ObjectFactory objectFactory,
      NativeImplLoader nativeImplLoader, FullPathResolver fullPathResolver) {
    this.objectFactory = objectFactory;
    this.nativeImplLoader = nativeImplLoader;
    this.fullPathResolver = fullPathResolver;
  }

  public ExpressionToTaskConverter get(Definitions definitions) {
    return new ExpressionToTaskConverter(
        definitions, objectFactory, nativeImplLoader, fullPathResolver);
  }
}

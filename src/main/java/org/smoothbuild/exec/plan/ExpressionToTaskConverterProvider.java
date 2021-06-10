package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.exec.nativ.NativeLoader;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.lang.base.define.Definitions;

public class ExpressionToTaskConverterProvider {
  private final ObjectFactory objectFactory;
  private final NativeLoader nativeLoader;
  private final FullPathResolver fullPathResolver;

  @Inject
  public ExpressionToTaskConverterProvider(ObjectFactory objectFactory,
      NativeLoader nativeLoader, FullPathResolver fullPathResolver) {
    this.objectFactory = objectFactory;
    this.nativeLoader = nativeLoader;
    this.fullPathResolver = fullPathResolver;
  }

  public ExpressionToTaskConverter get(Definitions definitions) {
    return new ExpressionToTaskConverter(
        definitions, objectFactory, nativeLoader, fullPathResolver);
  }
}

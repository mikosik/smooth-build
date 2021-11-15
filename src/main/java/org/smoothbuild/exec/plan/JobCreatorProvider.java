package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.DefinitionsS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypingS;

public class JobCreatorProvider {
  private final TypeSToTypeOConverter typeSToTypeOConverter;
  private final MethodLoader methodLoader;
  private final TypeFactoryS factory;
  private final TypingS typing;

  @Inject
  public JobCreatorProvider(TypeSToTypeOConverter typeSToTypeOConverter, MethodLoader methodLoader,
      TypeFactoryS factory, TypingS typing) {
    this.typeSToTypeOConverter = typeSToTypeOConverter;
    this.methodLoader = methodLoader;
    this.factory = factory;
    this.typing = typing;
  }

  public JobCreator get(DefinitionsS definitions) {
    return new JobCreator(definitions, typeSToTypeOConverter, methodLoader, factory, typing);
  }
}

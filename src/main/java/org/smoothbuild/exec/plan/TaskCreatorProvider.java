package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;

public class TaskCreatorProvider {
  private final STypeToOTypeConverter STypeToOTypeConverter;
  private final MethodLoader methodLoader;
  private final TypeFactoryS factory;
  private final Typing typing;

  @Inject
  public TaskCreatorProvider(STypeToOTypeConverter STypeToOTypeConverter, MethodLoader methodLoader,
      TypeFactoryS factory, Typing typing) {
    this.STypeToOTypeConverter = STypeToOTypeConverter;
    this.methodLoader = methodLoader;
    this.factory = factory;
    this.typing = typing;
  }

  public JobCreator get(Definitions definitions) {
    return new JobCreator(definitions, STypeToOTypeConverter, methodLoader, factory, typing);
  }
}

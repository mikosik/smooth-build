package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.impl.STypeFactory;

public class TaskCreatorProvider {
  private final STypeToOTypeConverter STypeToOTypeConverter;
  private final MethodLoader methodLoader;
  private final STypeFactory factory;
  private final Typing typing;

  @Inject
  public TaskCreatorProvider(STypeToOTypeConverter STypeToOTypeConverter, MethodLoader methodLoader,
      STypeFactory factory, Typing typing) {
    this.STypeToOTypeConverter = STypeToOTypeConverter;
    this.methodLoader = methodLoader;
    this.factory = factory;
    this.typing = typing;
  }

  public JobCreator get(Definitions definitions) {
    return new JobCreator(definitions, STypeToOTypeConverter, methodLoader, factory, typing);
  }
}

package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.api.TypeFactory;

public class TaskCreatorProvider {
  private final TypeToSpecConverter typeToSpecConverter;
  private final MethodLoader methodLoader;
  private final TypeFactory factory;
  private final Typing typing;

  @Inject
  public TaskCreatorProvider(TypeToSpecConverter typeToSpecConverter, MethodLoader methodLoader,
      TypeFactory factory, Typing typing) {
    this.typeToSpecConverter = typeToSpecConverter;
    this.methodLoader = methodLoader;
    this.factory = factory;
    this.typing = typing;
  }

  public JobCreator get(Definitions definitions) {
    return new JobCreator(definitions, typeToSpecConverter, methodLoader, factory, typing);
  }
}

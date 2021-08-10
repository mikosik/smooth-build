package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.Definitions;

public class TaskCreatorProvider {
  private final TypeToSpecConverter typeToSpecConverter;
  private final MethodLoader methodLoader;

  @Inject
  public TaskCreatorProvider(TypeToSpecConverter typeToSpecConverter, MethodLoader methodLoader) {
    this.typeToSpecConverter = typeToSpecConverter;
    this.methodLoader = methodLoader;
  }

  public TaskCreator get(Definitions definitions) {
    return new TaskCreator(definitions, typeToSpecConverter, methodLoader);
  }
}

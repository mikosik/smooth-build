package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Task;

public interface DefinitionNode {

  public abstract CodeLocation codeLocation();

  public abstract Type type();

  public abstract Task generateTask();

}

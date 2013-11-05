package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Taskable;

public interface Node extends Taskable {
  public Type type();

  public CodeLocation codeLocation();
}

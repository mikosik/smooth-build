package org.smoothbuild.function.def;

import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.exec.TaskGenerator;

public interface LocatedNode extends Node {
  @Override
  public LocatedTask generateTask(TaskGenerator taskGenerator);
}

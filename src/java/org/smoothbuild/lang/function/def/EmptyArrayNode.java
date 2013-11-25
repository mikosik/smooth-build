package org.smoothbuild.lang.function.def;

import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;

import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.EmptyArrayTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class EmptyArrayNode extends Node {

  public EmptyArrayNode(CodeLocation codeLocation) {
    super(EMPTY_ARRAY, codeLocation);
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    return new EmptyArrayTask(codeLocation());
  }
}

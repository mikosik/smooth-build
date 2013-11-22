package org.smoothbuild.lang.function.def;

import static org.smoothbuild.lang.type.Type.EMPTY_ARRAY;

import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.EmptySetTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class EmptySetNode extends Node {

  public EmptySetNode(CodeLocation codeLocation) {
    super(EMPTY_ARRAY, codeLocation);
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    return new EmptySetTask(codeLocation());
  }
}

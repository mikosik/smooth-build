package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.EMPTY_SET;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.EmptySetTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class EmptySetNode extends AbstractNode {

  public EmptySetNode(CodeLocation codeLocation) {
    super(EMPTY_SET, codeLocation);
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    return new EmptySetTask(codeLocation());
  }
}

package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.EMPTY_SET;
import static org.smoothbuild.task.base.Constants.SET_TASK_NAME;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.EmptySetTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class EmptySetNode extends AbstractDefinitionNode {
  public EmptySetNode(CodeLocation codeLocation) {
    super(CallLocation.callLocation(SET_TASK_NAME, codeLocation));
  }

  @Override
  public Type type() {
    return EMPTY_SET;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    return new EmptySetTask();
  }
}

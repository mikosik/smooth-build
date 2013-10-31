package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.EMPTY_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.EmptySetTask;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.exec.TaskGenerator;

public class EmptySetNode extends AbstractNode {
  public EmptySetNode(CodeLocation codeLocation) {
    super(codeLocation);
  }

  @Override
  public Type type() {
    return EMPTY_SET;
  }

  @Override
  public LocatedTask generateTask(TaskGenerator taskGenerator) {
    EmptySetTask task = new EmptySetTask();
    return new LocatedTask(task, codeLocation());
  }
}

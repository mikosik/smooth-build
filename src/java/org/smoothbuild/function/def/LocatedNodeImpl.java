package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.exec.TaskGenerator;

public class LocatedNodeImpl implements LocatedNode {
  private final Node node;
  private final CodeLocation codeLocation;

  public LocatedNodeImpl(Node node, CodeLocation codeLocation) {
    this.node = checkNotNull(node);
    this.codeLocation = checkNotNull(codeLocation);
  }

  @Override
  public LocatedTask generateTask(TaskGenerator taskGenerator) {
    return new LocatedTask(node.generateTask(taskGenerator), codeLocation);
  }

  @Override
  public Type type() {
    return node.type();
  }
}

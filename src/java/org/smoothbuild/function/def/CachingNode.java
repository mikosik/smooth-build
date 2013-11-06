package org.smoothbuild.function.def;

import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class CachingNode extends Node {
  private final Node abstractNode;
  private Task cachedTask;

  public CachingNode(Node node) {
    super(node.type(), node.codeLocation());
    this.abstractNode = node;
    this.cachedTask = null;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    if (cachedTask == null) {
      cachedTask = abstractNode.generateTask(taskGenerator);
    }
    return cachedTask;
  }
}

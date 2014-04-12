package org.smoothbuild.lang.function.def;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class CachingNode<T extends SValue> extends Node<T> {
  private final Node<T> abstractNode;
  private Task<T> cachedTask;

  public CachingNode(Node<T> node) {
    super(node.type(), node.codeLocation());
    this.abstractNode = node;
    this.cachedTask = null;
  }

  @Override
  public Task<T> generateTask(TaskGenerator taskGenerator) {
    if (cachedTask == null) {
      cachedTask = abstractNode.generateTask(taskGenerator);
    }
    return cachedTask;
  }
}

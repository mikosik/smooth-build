package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.exec.TaskGenerator;

public class CachingNode implements LocatedNode {
  private final LocatedNode node;
  private LocatedTask cachedTask;

  public CachingNode(LocatedNode node) {
    this.node = node;
    this.cachedTask = null;
  }

  @Override
  public Type type() {
    return node.type();
  }

  @Override
  public LocatedTask generateTask(TaskGenerator taskGenerator) {
    if (cachedTask == null) {
      cachedTask = node.generateTask(taskGenerator);
    }
    return cachedTask;
  }
}

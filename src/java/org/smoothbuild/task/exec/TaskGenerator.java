package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.task.base.Task;

import com.google.common.hash.HashCode;

public class TaskGenerator {
  private final HashedTasks hashedTasks;

  @Inject
  public TaskGenerator(HashedTasks hashedTasks) {
    this.hashedTasks = hashedTasks;
  }

  public HashCode generateTask(DefinitionNode node) {
    Task task = node.generateTask(this);
    hashedTasks.add(task);
    return task.hash();
  }
}

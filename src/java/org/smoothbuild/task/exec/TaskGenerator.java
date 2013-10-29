package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;

public class TaskGenerator {
  private final TaskExecutor taskExecutor;

  @Inject
  public TaskGenerator(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  public Result generateTask(DefinitionNode node) {
    Task task = node.generateTask(this);
    return new TaskContainer(taskExecutor, task, node.callLocation());
  }
}

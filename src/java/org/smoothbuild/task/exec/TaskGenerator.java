package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.base.Result;

public class TaskGenerator {
  private final TaskExecutor taskExecutor;

  @Inject
  public TaskGenerator(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  public Result generateTask(DefinitionNode node) {
    LocatedTask task = node.generateTask(this);
    return new TaskContainer(taskExecutor, task);
  }
}

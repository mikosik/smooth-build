package org.smoothbuild.task.exec;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.Taskable;

import com.google.common.collect.Maps;

public class TaskGenerator {
  private final TaskExecutor taskExecutor;
  private final Map<Task, TaskContainer> map;

  @Inject
  public TaskGenerator(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
    this.map = Maps.newHashMap();
  }

  public Result generateTask(Taskable taskable) {
    Task task = taskable.generateTask(this);
    TaskContainer taskContainer = map.get(task);
    if (taskContainer != null) {
      return taskContainer;
    }

    taskContainer = new TaskContainer(taskExecutor, task);
    map.put(task, taskContainer);

    return taskContainer;
  }
}

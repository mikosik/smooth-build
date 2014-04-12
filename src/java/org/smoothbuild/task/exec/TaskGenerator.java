package org.smoothbuild.task.exec;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.Taskable;

import com.google.common.collect.Maps;

public class TaskGenerator {
  private final TaskContainerCreator taskContainerCreator;
  private final Map<Task<?>, TaskContainer<?>> map;

  @Inject
  public TaskGenerator(TaskContainerCreator taskContainerCreator) {
    this.taskContainerCreator = taskContainerCreator;
    this.map = Maps.newHashMap();
  }

  public <T extends SValue> Result<T> generateTask(Taskable<T> taskable) {
    Task<T> task = taskable.generateTask(this);

    /*
     * This is safe as we only put proper types into a map.
     */
    @SuppressWarnings("unchecked")
    TaskContainer<T> taskContainer = (TaskContainer<T>) map.get(task);
    if (taskContainer != null) {
      return taskContainer;
    }

    taskContainer = taskContainerCreator.create(task);
    map.put(task, taskContainer);

    return taskContainer;
  }
}

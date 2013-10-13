package org.smoothbuild.task.exec;

import java.util.Map;

import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class HashedTasks {
  private final ImmutableMap<HashCode, Task> tasks;

  public HashedTasks(Map<HashCode, Task> tasks) {
    this.tasks = ImmutableMap.copyOf(tasks);
  }

  public Task get(HashCode hash) {
    Task task = tasks.get(hash);
    if (task == null) {
      throw new NoTaskWithGivenHashException(hash);
    }
    return task;
  }
}

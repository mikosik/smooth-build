package org.smoothbuild.task.exec;

import java.util.Map;

import org.smoothbuild.task.base.Task;

import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;

public class HashedTasks {
  private final Map<HashCode, Task> tasks;

  public HashedTasks(Map<HashCode, Task> tasks) {
    this();
    this.tasks.putAll(tasks);
  }

  public HashedTasks() {
    this.tasks = Maps.newHashMap();
  }

  public void add(Task task) {
    HashCode hash = task.hash();
    if (tasks.get(hash) == null) {
      tasks.put(hash, task);
    }
  }

  public Task get(HashCode hash) {
    Task task = tasks.get(hash);
    if (task == null) {
      throw new NoTaskWithGivenHashException(hash);
    }
    return task;
  }
}

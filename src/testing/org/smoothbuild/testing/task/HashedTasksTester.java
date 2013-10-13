package org.smoothbuild.testing.task;

import org.smoothbuild.task.HashedTasks;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class HashedTasksTester {

  public static HashedTasks hashedTasks(Task... tasks) {
    Builder<HashCode, Task> builder = ImmutableMap.builder();
    for (Task task : tasks) {
      builder.put(task.hash(), task);
    }
    ImmutableMap<HashCode, Task> map = builder.build();

    return new HashedTasks(map);
  }
}

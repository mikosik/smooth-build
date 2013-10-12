package org.smoothbuild.testing.task;

import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;

public class TaskTester {
  public static ImmutableList<HashCode> hashes(Task... tasks) {
    Builder<HashCode> builder = ImmutableList.builder();
    for (Task task : tasks) {
      builder.add(task.hash());
    }
    return builder.build();
  }
}

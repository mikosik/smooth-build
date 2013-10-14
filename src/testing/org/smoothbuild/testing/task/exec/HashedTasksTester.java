package org.smoothbuild.testing.task.exec;

import java.util.Map;

import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.HashedTasks;

import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;

public class HashedTasksTester {

  public static HashedTasks hashedTasks(Task... tasks) {
    Map<HashCode, Task> alreadyAdded = Maps.newHashMap();
    HashedTasks result = new HashedTasks();
    for (Task task : tasks) {
      HashCode hash = task.hash();
      if (alreadyAdded.containsKey(hash)) {
        throw new IllegalArgumentException("Two tasks with the same hash cannot be added: " + task
            + " " + alreadyAdded.get(hash));
      }
      alreadyAdded.put(hash, task);

      result.add(task);
    }
    return result;
  }
}

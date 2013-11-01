package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.task.TaskDb;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;

import com.google.common.hash.HashCode;

public class CachingTask implements Task {
  private final TaskDb taskDb;
  private final NativeCallHasher nativeCallHasher;
  private final Task task;

  public CachingTask(TaskDb taskDb, NativeCallHasher nativeCallHasher, Task task) {
    this.taskDb = checkNotNull(taskDb);
    this.nativeCallHasher = checkNotNull(nativeCallHasher);
    this.task = checkNotNull(task);
  }

  @Override
  public String name() {
    return task.name();
  }

  @Override
  public boolean isInternal() {
    return task.isInternal();
  }

  @Override
  public Value execute(Sandbox sandbox) {
    HashCode hash = nativeCallHasher.hash();
    if (taskDb.contains(hash)) {
      return taskDb.read(hash);
    }

    Value result = task.execute(sandbox);

    // TODO Remove null checking once Void is no longer allowed return type for
    // smooth functions. This will happen when save() function is removed.
    if (result != null) {
      taskDb.store(hash, result);
    }
    return result;
  }
}

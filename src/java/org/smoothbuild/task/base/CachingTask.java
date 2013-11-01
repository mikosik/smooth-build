package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.result.ResultDb;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;

import com.google.common.hash.HashCode;

public class CachingTask implements Task {
  private final ResultDb resultDb;
  private final NativeCallHasher nativeCallHasher;
  private final Task task;

  public CachingTask(ResultDb resultDb, NativeCallHasher nativeCallHasher, Task task) {
    this.resultDb = checkNotNull(resultDb);
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
    if (resultDb.contains(hash)) {
      return resultDb.read(hash);
    }

    Value result = task.execute(sandbox);

    // TODO Remove null checking once Void is no longer allowed return type for
    // smooth functions. This will happen when save() function is removed.
    if (result != null) {
      resultDb.store(hash, result);
    }
    return result;
  }
}

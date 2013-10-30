package org.smoothbuild.task.base;

import org.smoothbuild.db.ResultDb;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;

import com.google.common.hash.HashCode;

public class CachingTask implements Task {
  private final ResultDb resultDb;
  private final NativeCallHasher nativeCallHasher;
  private final Task task;

  public CachingTask(ResultDb resultDb, NativeCallHasher nativeCallHasher, Task task) {
    this.resultDb = resultDb;
    this.nativeCallHasher = nativeCallHasher;
    this.task = task;
  }

  @Override
  public String name() {
    return task.name();
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

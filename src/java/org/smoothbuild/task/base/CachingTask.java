package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.io.db.task.CachedResult;
import org.smoothbuild.io.db.task.TaskDb;
import org.smoothbuild.lang.function.base.CallHasher;
import org.smoothbuild.lang.plugin.Value;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.hash.HashCode;

public class CachingTask extends Task {
  private final TaskDb taskDb;
  private final CallHasher callHasher;
  private final Task task;

  public CachingTask(TaskDb taskDb, CallHasher callHasher, Task task) {
    super(task.name(), task.isInternal(), task.codeLocation());
    this.taskDb = checkNotNull(taskDb);
    this.callHasher = checkNotNull(callHasher);
    this.task = checkNotNull(task);
  }

  @Override
  public Value execute(SandboxImpl sandbox) {
    HashCode hash = callHasher.hash();
    if (taskDb.contains(hash)) {
      sandbox.messageGroup().setResultIsFromCache();
      CachedResult cachedResult = taskDb.read(hash);
      for (Message message : cachedResult.messages()) {
        sandbox.report(message);
      }
      return cachedResult.value();
    }

    Value result = task.execute(sandbox);

    // TODO Remove null checking once Void is no longer allowed return type for
    // smooth functions. This will happen when save() function is removed.
    if (result != null) {
      taskDb.store(hash, new CachedResult(result, sandbox.messageGroup()));
    }
    return result;
  }
}

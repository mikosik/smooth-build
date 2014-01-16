package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.io.cache.task.CachedResult;
import org.smoothbuild.io.cache.task.TaskDb;
import org.smoothbuild.lang.function.base.CallHasher;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.PluginApiImpl;

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
  public SValue execute(PluginApiImpl pluginApi) {
    HashCode hash = callHasher.hash();
    if (taskDb.contains(hash)) {
      pluginApi.setResultIsFromCache();
      CachedResult cachedResult = taskDb.read(hash);
      for (Message message : cachedResult.messages()) {
        pluginApi.report(message);
      }
      return cachedResult.value();
    }

    SValue result = task.execute(pluginApi);
    taskDb.store(hash, new CachedResult(result, pluginApi.messages()));
    return result;
  }
}

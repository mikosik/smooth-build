package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.io.cache.task.CachedResult;
import org.smoothbuild.io.cache.task.TaskDb;
import org.smoothbuild.lang.function.base.CallHasher;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.PluginApiImpl;

import com.google.common.hash.HashCode;

public class CachingTask<T extends SValue> extends Task<T> {
  private final TaskDb taskDb;
  private final CallHasher<T> callHasher;
  private final Task<T> task;

  public CachingTask(TaskDb taskDb, CallHasher<T> callHasher, Task<T> task) {
    super(task.resultType(), task.name(), task.isInternal(), task.codeLocation());
    this.taskDb = checkNotNull(taskDb);
    this.callHasher = checkNotNull(callHasher);
    this.task = checkNotNull(task);
  }

  @Override
  public T execute(PluginApiImpl pluginApi) {
    HashCode hash = callHasher.hash();
    if (taskDb.contains(hash)) {
      return readFromCache(pluginApi, hash);
    } else {
      return executeAndCache(pluginApi, hash);
    }
  }

  private T readFromCache(PluginApiImpl pluginApi, HashCode hash) {
    pluginApi.setResultIsFromCache();
    CachedResult<T> cachedResult = taskDb.read(hash, task.resultType());
    for (Message message : cachedResult.messages()) {
      pluginApi.log(message);
    }
    return cachedResult.value();
  }

  private T executeAndCache(PluginApiImpl pluginApi, HashCode hash) {
    T result = task.execute(pluginApi);
    taskDb.store(hash, new CachedResult<T>(result, pluginApi.loggedMessages()));
    return result;
  }
}

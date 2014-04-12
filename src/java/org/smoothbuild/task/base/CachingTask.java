package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.io.cache.task.CachedResult;
import org.smoothbuild.io.cache.task.TaskDb;
import org.smoothbuild.lang.function.base.CallHasher;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.NativeApiImpl;

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
  public T execute(NativeApiImpl nativeApi) {
    HashCode hash = callHasher.hash();
    if (taskDb.contains(hash)) {
      return readFromCache(nativeApi, hash);
    } else {
      return executeAndCache(nativeApi, hash);
    }
  }

  private T readFromCache(NativeApiImpl nativeApi, HashCode hash) {
    nativeApi.setResultIsFromCache();
    CachedResult<T> cachedResult = taskDb.read(hash, task.resultType());
    for (Message message : cachedResult.messages()) {
      nativeApi.log(message);
    }
    return cachedResult.value();
  }

  private T executeAndCache(NativeApiImpl nativeApi, HashCode hash) {
    T result = task.execute(nativeApi);
    taskDb.store(hash, new CachedResult<T>(result, nativeApi.loggedMessages()));
    return result;
  }
}

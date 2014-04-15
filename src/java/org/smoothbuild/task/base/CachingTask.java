package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.taskresults.TaskResult;
import org.smoothbuild.db.taskresults.TaskResultsDb;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.CallHasher;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.hash.HashCode;

public class CachingTask<T extends SValue> extends Task<T> {
  private final TaskResultsDb taskResultsDb;
  private final CallHasher<T> callHasher;
  private final Task<T> task;

  public CachingTask(TaskResultsDb taskResultsDb, CallHasher<T> callHasher, Task<T> task) {
    super(task.resultType(), task.name(), task.isInternal(), task.codeLocation());
    this.taskResultsDb = checkNotNull(taskResultsDb);
    this.callHasher = checkNotNull(callHasher);
    this.task = checkNotNull(task);
  }

  @Override
  public T execute(NativeApiImpl nativeApi) {
    HashCode hash = callHasher.hash();
    if (taskResultsDb.contains(hash)) {
      return readFromCache(nativeApi, hash);
    } else {
      return executeAndCache(nativeApi, hash);
    }
  }

  private T readFromCache(NativeApiImpl nativeApi, HashCode hash) {
    nativeApi.setResultIsFromCache();
    TaskResult<T> taskResult = taskResultsDb.read(hash, task.resultType());
    for (Message message : taskResult.messages()) {
      nativeApi.log(message);
    }
    return taskResult.value();
  }

  private T executeAndCache(NativeApiImpl nativeApi, HashCode hash) {
    T result = task.execute(nativeApi);
    taskResultsDb.store(hash, new TaskResult<T>(result, nativeApi.loggedMessages()));
    return result;
  }
}

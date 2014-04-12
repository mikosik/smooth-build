package org.smoothbuild.task.exec;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.base.Task;

public class TaskExecutor {
  private final Provider<NativeApiImpl> nativeApiProvider;
  private final TaskReporter taskReporter;

  @Inject
  public TaskExecutor(Provider<NativeApiImpl> nativeApiProvider, TaskReporter taskReporter) {
    this.nativeApiProvider = nativeApiProvider;
    this.taskReporter = taskReporter;
  }

  public <T extends SValue> T execute(Task<T> task) {
    NativeApiImpl nativeApi = nativeApiProvider.get();
    T result = task.execute(nativeApi);
    taskReporter.report(task, nativeApi);

    if (nativeApi.loggedMessages().containsProblems()) {
      throw new BuildInterruptedException();
    }

    return result;
  }
}

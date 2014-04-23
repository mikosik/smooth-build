package org.smoothbuild.task.exec;

import static org.smoothbuild.message.base.Messages.containsProblems;

import javax.inject.Inject;

import org.smoothbuild.db.taskresults.TaskResult;
import org.smoothbuild.db.taskresults.TaskResultsDb;
import org.smoothbuild.lang.base.SValue;

import com.google.common.hash.HashCode;

public class TaskExecutor {
  private final NativeApiImpl nativeApi;
  private final TaskResultsDb taskResultsDb;
  private final TaskReporter reporter;

  @Inject
  public TaskExecutor(NativeApiImpl nativeApi, TaskResultsDb taskResultsDb, TaskReporter reporter) {
    this.nativeApi = nativeApi;
    this.taskResultsDb = taskResultsDb;
    this.reporter = reporter;
  }

  public <T extends SValue> void execute(Task<T> task) {
    HashCode hash = task.hash();
    boolean isAlreadyCached = taskResultsDb.contains(hash);
    if (isAlreadyCached) {
      TaskResult<T> output = taskResultsDb.read(hash, task.resultType());
      task.setOutput(output);
    } else {
      task.execute(nativeApi);
      if (task.isCacheable()) {
        taskResultsDb.write(hash, task.output());
      }
    }
    reporter.report(task, isAlreadyCached);
    if (containsProblems(task.output().messages())) {
      throw new BuildInterruptedException();
    }
  }
}

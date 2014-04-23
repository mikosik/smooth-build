package org.smoothbuild.task.exec;

import static org.smoothbuild.message.base.Messages.containsProblems;

import javax.inject.Inject;

import org.smoothbuild.db.taskoutputs.TaskOutput;
import org.smoothbuild.db.taskoutputs.TaskOutputsDb;
import org.smoothbuild.lang.base.SValue;

import com.google.common.hash.HashCode;

public class TaskExecutor {
  private final NativeApiImpl nativeApi;
  private final TaskOutputsDb taskOutputsDb;
  private final TaskReporter reporter;

  @Inject
  public TaskExecutor(NativeApiImpl nativeApi, TaskOutputsDb taskOutputsDb, TaskReporter reporter) {
    this.nativeApi = nativeApi;
    this.taskOutputsDb = taskOutputsDb;
    this.reporter = reporter;
  }

  public <T extends SValue> void execute(Task<T> task) {
    HashCode hash = task.hash();
    boolean isAlreadyCached = taskOutputsDb.contains(hash);
    if (isAlreadyCached) {
      TaskOutput<T> output = taskOutputsDb.read(hash, task.resultType());
      task.setOutput(output);
    } else {
      task.execute(nativeApi);
      if (task.isCacheable()) {
        taskOutputsDb.write(hash, task.output());
      }
    }
    reporter.report(task, isAlreadyCached);
    if (containsProblems(task.output().messages())) {
      throw new BuildInterruptedException();
    }
  }
}

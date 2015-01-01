package org.smoothbuild.task.exec;

import static org.smoothbuild.message.base.Messages.containsProblems;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.taskoutputs.TaskOutputsDb;
import org.smoothbuild.io.util.SmoothJar;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.TaskOutput;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class TaskExecutor {
  private final HashCode smoothJarHash;
  private final NativeApiImpl nativeApi;
  private final TaskOutputsDb taskOutputsDb;
  private final TaskReporter reporter;

  @Inject
  public TaskExecutor(@SmoothJar HashCode smoothJarHash, NativeApiImpl nativeApi,
      TaskOutputsDb taskOutputsDb, TaskReporter reporter) {
    this.smoothJarHash = smoothJarHash;
    this.nativeApi = nativeApi;
    this.taskOutputsDb = taskOutputsDb;
    this.reporter = reporter;
  }

  public <T extends Value> void execute(Task task) {
    HashCode hash = taskHash(task);
    boolean isAlreadyCached = taskOutputsDb.contains(hash);
    if (isAlreadyCached) {
      TaskOutput output = taskOutputsDb.read(hash, task.resultType());
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

  private <T extends Value> HashCode taskHash(Task task) {
    Hasher hasher = Hash.newHasher();
    hasher.putBytes(smoothJarHash.asBytes());
    hasher.putBytes(task.hash().asBytes());
    return hasher.hash();
  }
}

package org.smoothbuild.task.exec;

import static org.smoothbuild.lang.message.Messages.containsErrors;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.outputs.OutputsDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.util.SmoothJar;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class TaskExecutor {
  private final HashCode smoothJarHash;
  private final OutputsDb outputsDb;
  private final TaskReporter reporter;
  private final FileSystem projectFileSystem;
  private final ValuesDb valuesDb;
  private final TempManager tempManager;

  @Inject
  public TaskExecutor(@SmoothJar HashCode smoothJarHash, OutputsDb outputsDb,
      TaskReporter reporter, @ProjectDir FileSystem projectFileSystem, ValuesDb valuesDb,
      TempManager tempManager) {
    this.smoothJarHash = smoothJarHash;
    this.outputsDb = outputsDb;
    this.reporter = reporter;
    this.projectFileSystem = projectFileSystem;
    this.valuesDb = valuesDb;
    this.tempManager = tempManager;
  }

  public <T extends Value> void execute(Task task) {
    HashCode hash = taskHash(task);
    boolean isAlreadyCached = outputsDb.contains(hash);
    if (isAlreadyCached) {
      Output output = outputsDb.read(hash, task.resultType());
      task.setOutput(output);
    } else {
      ContainerImpl container = new ContainerImpl(projectFileSystem, valuesDb, tempManager);
      task.execute(container);
      container.destroy();
      if (task.isCacheable()) {
        outputsDb.write(hash, task.output());
      }
    }
    reporter.report(task, isAlreadyCached);
    if (containsErrors(task.output().messages())) {
      throw new ExecutionException();
    }
  }

  private <T extends Value> HashCode taskHash(Task task) {
    Hasher hasher = Hash.newHasher();
    hasher.putBytes(smoothJarHash.asBytes());
    hasher.putBytes(task.hash().asBytes());
    return hasher.hash();
  }
}

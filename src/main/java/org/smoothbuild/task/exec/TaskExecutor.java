package org.smoothbuild.task.exec;

import static org.smoothbuild.message.base.Messages.containsProblems;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.db.taskoutputs.TaskOutputsDb;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.util.SmoothJar;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class TaskExecutor {
  private final HashCode smoothJarHash;
  private final TaskOutputsDb taskOutputsDb;
  private final TaskReporter reporter;
  private final FileSystem projectFileSystem;
  private final ObjectsDb objectsDb;
  private final Provider<TempDirectory> tempDirectoryProvider;

  @Inject
  public TaskExecutor(@SmoothJar HashCode smoothJarHash, TaskOutputsDb taskOutputsDb,
      TaskReporter reporter, @ProjectDir FileSystem projectFileSystem, ObjectsDb objectsDb,
      Provider<TempDirectory> tempDirectoryProvider) {
    this.smoothJarHash = smoothJarHash;
    this.taskOutputsDb = taskOutputsDb;
    this.reporter = reporter;
    this.projectFileSystem = projectFileSystem;
    this.objectsDb = objectsDb;
    this.tempDirectoryProvider = tempDirectoryProvider;
  }

  public <T extends Value> void execute(Task task) {
    HashCode hash = taskHash(task);
    boolean isAlreadyCached = taskOutputsDb.contains(hash);
    if (isAlreadyCached) {
      Output output = taskOutputsDb.read(hash, task.resultType());
      task.setOutput(output);
    } else {
      ContainerImpl container = new ContainerImpl(projectFileSystem, objectsDb,
          tempDirectoryProvider);
      task.execute(container);
      container.destroy();
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

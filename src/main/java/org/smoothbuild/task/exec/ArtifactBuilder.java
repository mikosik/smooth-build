package org.smoothbuild.task.exec;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.Comparator.comparing;
import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.db.outputs.OutputsDbException;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.save.ArtifactSaver;

public class ArtifactBuilder {
  private final ArtifactSaver artifactSaver;
  private final TaskBatch taskBatch;
  private final Console console;
  private final Map<String, Task> artifacts;

  @Inject
  public ArtifactBuilder(ArtifactSaver artifactSaver, TaskBatch taskBatch, Console console) {
    this.artifactSaver = artifactSaver;
    this.taskBatch = taskBatch;
    this.console = console;
    this.artifacts = new TreeMap<>(comparing(String::toString));
  }

  public void addArtifact(Function function) {
    Expression expression = function.createCallExpression(list(), Location.unknownLocation());
    Task task = taskBatch.createTasks(expression);
    artifacts.put(function.name(), task);
  }

  public void runBuild() {
    try {
      taskBatch.executeAll();
    } catch (IOException e) {
      console.error("Execution failed due to I/O error. Caught exception:\n"
          + getStackTraceAsString(e));
    } catch (OutputsDbException e) {
      console.error("Execution failed due to Outputs DB error. Caught exception:\n"
          + getStackTraceAsString(e));
    }
    if (!taskBatch.containsErrors()) {
      console.println("\nbuilt artifact(s):");
      for (Entry<String, Task> artifact : artifacts.entrySet()) {
        save(artifact);
      }
    }
  }

  private void save(Entry<String, Task> artifact) {
    String name = artifact.getKey();
    SObject object = artifact.getValue().output().result();
    try {
      artifactSaver.save(name, object);
      console.println(name + " -> " + artifactPath(name));
    } catch (IOException e) {
      console.error("Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
          + getStackTraceAsString(e));
    }
  }
}

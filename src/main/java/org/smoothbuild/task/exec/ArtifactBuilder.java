package org.smoothbuild.task.exec;

import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.util.Lists.list;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.value.Value;
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
    this.artifacts = new TreeMap<>((name1, name2) -> name1.toString().compareTo(name2.toString()));
  }

  public void addArtifact(Function function) {
    Expression expression = function.createCallExpression(list(), Location.unknownLocation());
    Task task = taskBatch.createTasks(expression);
    artifacts.put(function.name(), task);
  }

  public void runBuild() {
    taskBatch.executeAll();
    if (!taskBatch.containsErrors()) {
      console.println("\nbuilt artifact(s):");
      for (Entry<String, Task> artifact : artifacts.entrySet()) {
        String name = artifact.getKey();
        Task task = artifact.getValue();
        Value value = task.output().result();
        artifactSaver.save(name, value);
        console.println(name.toString() + " -> " + artifactPath(name));
      }
    }
  }
}

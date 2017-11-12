package org.smoothbuild.task.exec;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.save.ArtifactSaver;
import org.smoothbuild.util.Dag;

public class ArtifactBuilder {
  private final ArtifactSaver artifactSaver;
  private final TaskBatch taskBatch;
  private final Map<Name, Dag<Task>> artifacts;

  @Inject
  public ArtifactBuilder(ArtifactSaver artifactSaver, TaskBatch taskBatch) {
    this.artifactSaver = artifactSaver;
    this.taskBatch = taskBatch;
    this.artifacts = new HashMap<>();
  }

  public void addArtifact(Function function) {
    Expression expression = function.createCallExpression(false, Location.commandLine());
    artifacts.put(function.name(), taskBatch.createTasks(new Dag<>(expression)));
  }

  public void runBuild() {
    taskBatch.executeAll();
    if (!taskBatch.containsErrors()) {
      for (Entry<Name, Dag<Task>> artifact : artifacts.entrySet()) {
        Name name = artifact.getKey();
        Task task = artifact.getValue().elem();
        Value value = task.output().result();
        artifactSaver.save(name, value);
      }
    }
  }
}

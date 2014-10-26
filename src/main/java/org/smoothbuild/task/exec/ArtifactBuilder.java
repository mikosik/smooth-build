package org.smoothbuild.task.exec;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.save.ArtifactSaver;
import org.smoothbuild.util.Empty;

import com.google.common.collect.Maps;

public class ArtifactBuilder {
  private final ArtifactSaver artifactSaver;
  private final TaskGraph taskGraph;
  private final Map<Name, Task<?>> artifacts;

  @Inject
  public ArtifactBuilder(ArtifactSaver artifactSaver, TaskGraph taskGraph) {
    this.artifactSaver = artifactSaver;
    this.taskGraph = taskGraph;
    this.artifacts = Maps.newHashMap();
  }

  public void addArtifact(Function<?> function) {
    Expression<?> expression = new CallExpression<>(function, false, CodeLocation.commandLine(),
        Empty.stringExprMap());
    artifacts.put(function.name(), taskGraph.createTasks(expression));
  }

  public void runBuild() {
    try {
      taskGraph.executeAll();
    } catch (BuildInterruptedException e) {
      return;
    }
    for (Entry<Name, Task<?>> artifact : artifacts.entrySet()) {
      Name name = artifact.getKey();
      Task<?> task = artifact.getValue();
      Value value = task.output().returnValue();
      artifactSaver.save(name, value);
    }
  }
}

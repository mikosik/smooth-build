package org.smoothbuild.task.exec;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.expr.CallExpr;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.save.ArtifactSaver;
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
    Expr<?> expr = new CallExpr<>(function, CodeLocation.commandLine(), Empty.stringExprMap());
    artifacts.put(function.name(), taskGraph.createTasks(expr));
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
      SValue value = task.output().returnValue();
      artifactSaver.save(name, value);
    }
  }
}

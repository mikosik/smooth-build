package org.smoothbuild.exec.task;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.exec.task.ArtifactPaths.artifactPath;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.db.outputs.OutputDbException;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.parse.expr.Expression;

public class ArtifactBuilder {
  private final ArtifactSaver artifactSaver;
  private final TaskBatch taskBatch;
  private final Console console;

  @Inject
  public ArtifactBuilder(ArtifactSaver artifactSaver, TaskBatch taskBatch, Console console) {
    this.artifactSaver = artifactSaver;
    this.taskBatch = taskBatch;
    this.console = console;
  }

  public void addArtifact(Function function) {
    Expression expression = function.createAgrlessCallExpression();
    taskBatch.createTasks(expression);
  }

  public void runBuild() {
    try {
      taskBatch.executeAll();
    } catch (IOException e) {
      console.error("Execution failed due to I/O error. Caught exception:\n"
          + getStackTraceAsString(e));
    } catch (OutputDbException e) {
      console.error("Execution failed due to Outputs DB error. Caught exception:\n"
          + getStackTraceAsString(e));
    }
    if (!taskBatch.containsErrors()) {
      console.println("\nbuilt artifact(s):");

      List<TaskNode> sortedTasks = taskBatch.rootTasks()
          .stream()
          .sorted(comparing(TaskNode::name))
          .collect(toList());
      for (TaskNode artifact : sortedTasks) {
        save(artifact);
      }
    }
  }

  private void save(TaskNode node) {
    String name = node.name();
    SObject object = node.result().output().value();
    try {
      artifactSaver.save(name, object);
      console.println(name + " -> " + artifactPath(name));
    } catch (IOException e) {
      console.error("Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
          + getStackTraceAsString(e));
    }
  }
}

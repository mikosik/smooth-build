package org.smoothbuild.exec.run.artifact;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.exec.run.artifact.ArtifactPaths.artifactPath;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.exec.task.base.Result;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.object.base.SObject;

import com.google.common.collect.ImmutableList;

public class ArtifactBuilder {
  private final ParallelTaskExecutor parallelExecutor;
  private final ArtifactSaver artifactSaver;
  private final Console console;

  @Inject
  public ArtifactBuilder(ParallelTaskExecutor parallelExecutor, ArtifactSaver artifactSaver,
      Console console) {
    this.parallelExecutor = parallelExecutor;
    this.artifactSaver = artifactSaver;
    this.console = console;
  }

  public void buildArtifacts(List<Function> functions) {
    ImmutableList<Task> tasks = functions.stream()
        .map(f -> f.createAgrlessCallExpression().createTask(null))
        .collect(toImmutableList());
    try {
      Map<Task, Result> artifacts = parallelExecutor.executeAll(tasks);
      if (allTasksCompletedSuccessfully(artifacts)) {
        console.println("\nbuilt artifact(s):");
        List<Entry<Task, Result>> sortedArtifacts = artifacts.entrySet()
            .stream()
            .sorted(comparing(e -> e.getKey().name()))
            .collect(toList());
        for (Entry<Task, Result> artifact : sortedArtifacts) {
          save(artifact.getKey().name(), artifact.getValue());
        }
      }
    } catch (InterruptedException e) {
      console.println("Build process has been interrupted.");
    }
  }

  private boolean allTasksCompletedSuccessfully(Map<Task, Result> artifacts) {
    return artifacts
        .values()
        .stream()
        .allMatch(result -> result != null && result.hasOutputWithValue());
  }

  private void save(String name, Result result) {
    SObject object = result.output().value();
    try {
      artifactSaver.save(name, object);
      console.println(name + " -> " + artifactPath(name));
    } catch (IOException e) {
      console.error("Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
          + getStackTraceAsString(e));
    }
  }
}

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
      Map<Task, SObject> artifacts = parallelExecutor.executeAll(tasks);
      if (!artifacts.containsValue(null)) {
        console.println("\nBuilt artifact(s):");
        List<Entry<Task, SObject>> sortedArtifacts = artifacts.entrySet()
            .stream()
            .sorted(comparing(e -> e.getKey().name()))
            .collect(toList());
        for (Entry<Task, SObject> artifact : sortedArtifacts) {
          save(artifact.getKey().name(), artifact.getValue());
        }
      }
    } catch (InterruptedException e) {
      console.println("Build process has been interrupted.");
    }
  }

  private void save(String name, SObject sObject) {
    try {
      artifactSaver.save(name, sObject);
      console.println("  " + name + " -> " + artifactPath(name));
    } catch (IOException e) {
      console.error("Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
          + getStackTraceAsString(e));
    }
  }
}

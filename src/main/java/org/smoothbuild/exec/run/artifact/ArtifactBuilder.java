package org.smoothbuild.exec.run.artifact;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.exec.run.artifact.ArtifactPaths.artifactPath;
import static org.smoothbuild.lang.base.Location.commandLineLocation;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor;
import org.smoothbuild.exec.task.plan.ExecutionPlanner;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.object.base.SObject;

import com.google.common.collect.ImmutableList;

public class ArtifactBuilder {
  private static final String SAVING_ARTIFACT_PHASE = "Saving artifact(s)";

  private final ParallelTaskExecutor parallelExecutor;
  private final ArtifactSaver artifactSaver;
  private final ExecutionPlanner executionPlanner;
  private final Reporter reporter;

  @Inject
  public ArtifactBuilder(ParallelTaskExecutor parallelExecutor, ArtifactSaver artifactSaver,
      ExecutionPlanner executionPlanner, Reporter reporter) {
    this.parallelExecutor = parallelExecutor;
    this.artifactSaver = artifactSaver;
    this.executionPlanner = executionPlanner;
    this.reporter = reporter;
  }

  public void buildArtifacts(List<Callable> callables) {
    ImmutableList<Task> tasks = callables.stream()
        .map(this::planFor)
        .collect(toImmutableList());
    try {
      Map<Task, SObject> artifacts = parallelExecutor.executeAll(tasks);
      if (!artifacts.containsValue(null)) {
        reporter.startNewPhase(SAVING_ARTIFACT_PHASE);
        List<Entry<Task, SObject>> sortedArtifacts = artifacts.entrySet()
            .stream()
            .sorted(comparing(e -> e.getKey().name()))
            .collect(toList());
        sortedArtifacts.forEach(this::save);
      }
    } catch (InterruptedException e) {
      reporter.startNewPhase(SAVING_ARTIFACT_PHASE);
      reporter.printlnRaw("Build process has been interrupted.");
    }
  }

  private Task planFor(Callable callable) {
    return executionPlanner
        .createPlan(callable.createAgrlessCallExpression(commandLineLocation()));
  }

  private void save(Entry<Task, SObject> artifact) {
    save(artifact.getKey().name(), artifact.getValue());
  }

  private void save(String name, SObject sObject) {
    try {
      Path path = artifactSaver.save(name, sObject);
      reportSuccess(name, path);
    } catch (IOException e) {
      reportFailure(name,
          "Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
              + getStackTraceAsString(e));
    } catch (DuplicatedPathsException e) {
      reportFailure(name, e.getMessage());
    }
  }

  private void reportSuccess(String name, Path path) {
    report(name, path.q(), List.of());
  }

  private void reportFailure(String name, String errorMessage) {
    report(name, "???", List.of(error(errorMessage)));
  }

  private void report(String name, String pathOrError, List<Log> logs) {
    String header = name + " -> " + pathOrError;
    reporter.report(header, logs);
  }
}

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
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.object.base.SObject;

import com.google.common.collect.ImmutableList;

public class ArtifactBuilder {
  private static final String SAVING_ARTIFACT_PHASE = "Saving artifact(s)";

  private final ParallelTaskExecutor parallelExecutor;
  private final ArtifactSaver artifactSaver;
  private final Reporter reporter;

  @Inject
  public ArtifactBuilder(ParallelTaskExecutor parallelExecutor, ArtifactSaver artifactSaver,
      Reporter reporter) {
    this.parallelExecutor = parallelExecutor;
    this.artifactSaver = artifactSaver;
    this.reporter = reporter;
  }

  public void buildArtifacts(List<Function> functions) {
    ImmutableList<Task> tasks = functions.stream()
        .map(f -> f.createAgrlessCallExpression(commandLineLocation()).createTask(null))
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

  private void save(Entry<Task, SObject> artifact) {
    save(artifact.getKey().name(), artifact.getValue());
  }

  private void save(String name, SObject sObject) {
    try {
      artifactSaver.save(name, sObject);
      reportArtifact(name);
    } catch (IOException e) {
      reportArtifact(name,
          "Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
              + getStackTraceAsString(e));
    } catch (DuplicatedPathsException e) {
      reportArtifact(name, e.getMessage());
    }
  }

  private void reportArtifact(String name) {
    reportArtifact(name, List.of());
  }

  private void reportArtifact(String name, String errorMessage) {
    reportArtifact(name, List.of(error(errorMessage)));
  }

  private void reportArtifact(String name, List<Log> logs) {
    String header = savingMessage(name, artifactPath(name).toString());
    reporter.report(header, logs);
  }

  private static String savingMessage(String name, String status) {
    return name + " -> " + status;
  }
}

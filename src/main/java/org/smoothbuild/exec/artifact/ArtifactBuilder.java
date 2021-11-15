package org.smoothbuild.exec.artifact;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.Comparator.comparing;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.exec.artifact.ArtifactPaths.artifactPath;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.exec.job.Job;
import org.smoothbuild.exec.parallel.ParallelJobExecutor;
import org.smoothbuild.exec.plan.ExecutionPlanner;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.define.DefinitionsS;
import org.smoothbuild.lang.base.define.ValueS;

public class ArtifactBuilder {
  private static final String SAVING_ARTIFACT_PHASE = "Saving artifact(s)";

  private final ParallelJobExecutor parallelExecutor;
  private final ArtifactSaver artifactSaver;
  private final ExecutionPlanner executionPlanner;
  private final Reporter reporter;

  @Inject
  public ArtifactBuilder(ParallelJobExecutor parallelExecutor, ArtifactSaver artifactSaver,
      ExecutionPlanner executionPlanner, Reporter reporter) {
    this.parallelExecutor = parallelExecutor;
    this.artifactSaver = artifactSaver;
    this.executionPlanner = executionPlanner;
    this.reporter = reporter;
  }

  public void buildArtifacts(DefinitionsS definitions, List<ValueS> values) {
    Map<ValueS, Job> plans = executionPlanner.createPlans(definitions, values);
    if (reporter.isProblemReported()) {
      return;
    }
    try {
      Map<ValueS, Optional<ObjectH>> artifacts = parallelExecutor.executeAll(plans);
      if (!artifacts.containsValue(Optional.<ObjectH>empty())) {
        reporter.startNewPhase(SAVING_ARTIFACT_PHASE);
        artifacts.entrySet()
            .stream()
            .sorted(comparing(e -> e.getKey().name()))
            .forEach(e -> save(e.getKey(), e.getValue().get()));
      }
    } catch (InterruptedException e) {
      reporter.startNewPhase(SAVING_ARTIFACT_PHASE);
      reporter.printlnRaw("Build process has been interrupted.");
    }
  }

  private void save(ValueS value, ObjectH obj) {
    String name = value.name();
    try {
      Path path = artifactSaver.save(value, obj);
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
    report(name, path.q(), list());
  }

  private void reportFailure(String name, String errorMessage) {
    report(name, "???", list(error(errorMessage)));
  }

  private void report(String name, String pathOrError, List<Log> logs) {
    String header = name + " -> " + pathOrError;
    reporter.report(header, logs);
  }
}

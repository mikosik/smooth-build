package org.smoothbuild.exec.run;

import static org.smoothbuild.exec.run.ValidateFunctionArguments.validateFunctionArguments;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.exec.run.artifact.ArtifactBuilder;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.runtime.SRuntime;

public class BuildRunner {
  private final ArtifactBuilder artifactBuilder;
  private final Reporter reporter;

  @Inject
  public BuildRunner(ArtifactBuilder artifactBuilder, Reporter reporter) {
    this.artifactBuilder = artifactBuilder;
    this.reporter = reporter;
  }

  public void execute(SRuntime runtime, List<String> names) {
    reporter.newSection("Building");
    List<Function> functionsToRun = validateFunctionArguments(reporter, runtime, names);
    if (!reporter.isProblemReported()) {
      artifactBuilder.buildArtifacts(functionsToRun);
    }
  }
}

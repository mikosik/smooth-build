package org.smoothbuild.exec.run;

import static org.smoothbuild.exec.run.ValidateFunctionArguments.validateFunctionArguments;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.exec.run.artifact.ArtifactBuilder;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.runtime.SRuntime;

public class BuildRunner {
  private final ArtifactBuilder artifactBuilder;
  private final Console console;

  @Inject
  public BuildRunner(ArtifactBuilder artifactBuilder, Console console) {
    this.artifactBuilder = artifactBuilder;
    this.console = console;
  }

  public void execute(SRuntime runtime, List<String> names) {
    console.println("Building");
    List<Function> functionsToRun = validateFunctionArguments(console, runtime, names);
    if (!console.isProblemReported()) {
      artifactBuilder.buildArtifacts(functionsToRun);
    }
  }
}

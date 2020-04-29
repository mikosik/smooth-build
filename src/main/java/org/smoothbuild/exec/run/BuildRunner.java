package org.smoothbuild.exec.run;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.exec.run.ArgumentValidator.validateFunctionNames;
import static org.smoothbuild.exec.run.ValidateFunctionArguments.validateFunctionArguments;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.exec.run.artifact.ArtifactBuilder;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.parse.RuntimeController;

public class BuildRunner {
  private final Console console;
  private final RuntimeController runtimeController;
  private final BuildExecutor buildExecutor;
  private final FileSystem fileSystem;

  @Inject
  public BuildRunner(Console console, RuntimeController runtimeController,
      BuildExecutor buildExecutor, FileSystem fileSystem) {
    this.console = console;
    this.runtimeController = runtimeController;
    this.buildExecutor = buildExecutor;
    this.fileSystem = fileSystem;
  }

  public int run(List<String> names) {
    List<String> errors = validateFunctionNames(names);
    if (!errors.isEmpty()) {
      console.errors(errors);
      return EXIT_CODE_ERROR;
    }

    for (Path path : list(ARTIFACTS_PATH, TEMPORARY_PATH)) {
      try {
        fileSystem.delete(path);
      } catch (IOException e) {
        console.error("Unable to delete " + path + ".");
        return EXIT_CODE_ERROR;
      }
    }

    return runtimeController.setUpRuntimeAndRun((runtime) -> buildExecutor.execute(runtime, names));
  }

  public static class BuildExecutor {
    private final ArtifactBuilder artifactBuilder;
    private final Reporter reporter;

    @Inject
    public BuildExecutor(ArtifactBuilder artifactBuilder, Reporter reporter) {
      this.artifactBuilder = artifactBuilder;
      this.reporter = reporter;
    }

    public void execute(SRuntime runtime, List<String> names) {
      reporter.startNewPhase("Building");
      List<Function> functionsToRun = validateFunctionArguments(reporter, runtime, names);
      if (!reporter.isProblemReported()) {
        artifactBuilder.buildArtifacts(functionsToRun);
      }
    }
  }
}

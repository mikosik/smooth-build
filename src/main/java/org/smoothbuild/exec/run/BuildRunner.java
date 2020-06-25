package org.smoothbuild.exec.run;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.exec.run.FindFunctions.findFunctions;
import static org.smoothbuild.exec.run.ValidateFunctionNames.validateFunctionNames;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.exec.run.artifact.ArtifactBuilder;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.parse.Definitions;
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
        console.error(
            "Unable to delete " + path + ". Caught exception:\n" + getStackTraceAsString(e));
        return EXIT_CODE_ERROR;
      }
    }

    return runtimeController.setUpRuntimeAndRun(
        (definitions) -> buildExecutor.execute(definitions, names));
  }

  public static class BuildExecutor {
    private final ArtifactBuilder artifactBuilder;
    private final Reporter reporter;

    @Inject
    public BuildExecutor(ArtifactBuilder artifactBuilder, Reporter reporter) {
      this.artifactBuilder = artifactBuilder;
      this.reporter = reporter;
    }

    public void execute(Definitions definitions, List<String> names) {
      reporter.startNewPhase("Building");
      findFunctions(reporter, definitions, names)
          .ifPresent(artifactBuilder::buildArtifacts);
    }
  }
}

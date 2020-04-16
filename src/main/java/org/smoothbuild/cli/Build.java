package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.cli.ArgumentValidator.validateFunctionNames;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.exec.run.BuildRunner;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.parse.RuntimeController;

public class Build {
  private final Console console;
  private final RuntimeController runtimeController;
  private final BuildRunner buildRunner;
  private final FileSystem fileSystem;

  @Inject
  public Build(Console console, RuntimeController runtimeController, BuildRunner buildRunner,
      FileSystem fileSystem) {
    this.console = console;
    this.runtimeController = runtimeController;
    this.buildRunner = buildRunner;
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

    return runtimeController.setUpRuntimeAndRun(
        (runtime) -> buildRunner.execute(runtime, names));
  }
}

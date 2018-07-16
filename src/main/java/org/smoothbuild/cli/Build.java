package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.cli.ArgumentValidator.validateFunctionNames;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.parse.RuntimeController;
import org.smoothbuild.task.exec.SmoothExecutor;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableList;

public class Build implements Command {
  private final Console console;
  private final RuntimeController runtimeController;
  private final SmoothExecutor smoothExecutor;
  private final FileSystem fileSystem;

  @Inject
  public Build(Console console, RuntimeController runtimeController, SmoothExecutor smoothExecutor,
      FileSystem fileSystem) {
    this.console = console;
    this.runtimeController = runtimeController;
    this.smoothExecutor = smoothExecutor;
    this.fileSystem = fileSystem;
  }

  @Override
  public int run(String... args) {
    List<String> argsWithoutFirst = ImmutableList.copyOf(args).subList(1, args.length);
    Maybe<Set<String>> functionNames = validateFunctionNames(argsWithoutFirst);
    if (!functionNames.hasValue()) {
      console.rawErrors(functionNames.errors());
      return EXIT_CODE_ERROR;
    }
    fileSystem.delete(ARTIFACTS_PATH);
    fileSystem.delete(TEMPORARY_PATH);
    return runtimeController.setUpRuntimeAndRun((runtime) -> {
      smoothExecutor.execute(runtime, functionNames.value());
    });
  }
}

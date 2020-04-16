package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.ArgumentValidator.validateFunctionNames;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.exec.run.TreeRunner;
import org.smoothbuild.parse.RuntimeController;

public class Tree {
  private final Console console;
  private final RuntimeController runtimeController;
  private final TreeRunner treeRunner;

  @Inject
  public Tree(Console console, RuntimeController runtimeController, TreeRunner treeRunner) {
    this.console = console;
    this.runtimeController = runtimeController;
    this.treeRunner = treeRunner;
  }

  public int run(List<String> names) {
    List<String> errors = validateFunctionNames(names);
    if (!errors.isEmpty()) {
      console.errors(errors);
      return EXIT_CODE_ERROR;
    }
    return runtimeController.setUpRuntimeAndRun(
        (runtime) -> treeRunner.execute(runtime, names));
  }
}

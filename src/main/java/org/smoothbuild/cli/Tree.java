package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.ArgumentValidator.validateFunctionNames;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.exec.run.TreeRunner;
import org.smoothbuild.parse.RuntimeController;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableList;

public class Tree implements Command {
  private final Console console;
  private final RuntimeController runtimeController;
  private final TreeRunner treeRunner;

  @Inject
  public Tree(Console console, RuntimeController runtimeController, TreeRunner treeRunner) {
    this.console = console;
    this.runtimeController = runtimeController;
    this.treeRunner = treeRunner;
  }

  @Override
  public int run(String... args) {
    List<String> argsWithoutFirst = ImmutableList.copyOf(args).subList(1, args.length);
    Maybe<Set<String>> functionNames = validateFunctionNames(argsWithoutFirst);
    if (!functionNames.hasValue()) {
      console.errors(functionNames.errors());
      return EXIT_CODE_ERROR;
    }
    return runtimeController.setUpRuntimeAndRun(
        (runtime) -> treeRunner.execute(runtime, functionNames.value()));
  }
}

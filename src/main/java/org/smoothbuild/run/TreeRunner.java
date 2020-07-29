package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.lang.base.Location.commandLineLocation;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.plan.ExecutionPlanner;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.parse.Definitions;

public class TreeRunner {
  private final Console console;
  private final RuntimeController runtimeController;
  private final TreeExecutor treeExecutor;

  @Inject
  public TreeRunner(Console console, RuntimeController runtimeController,
      TreeExecutor treeExecutor) {
    this.console = console;
    this.runtimeController = runtimeController;
    this.treeExecutor = treeExecutor;
  }

  public int run(List<String> names) {
    List<String> errors = ValidateFunctionNames.validateFunctionNames(names);
    if (!errors.isEmpty()) {
      console.errors(errors);
      return EXIT_CODE_ERROR;
    }
    return runtimeController.setUpRuntimeAndRun(
        (definitions) -> treeExecutor.execute(definitions, names));
  }

  public static class TreeExecutor {
    private final Reporter reporter;
    private final ExecutionPlanner executionPlanner;

    @Inject
    public TreeExecutor(Reporter reporter, ExecutionPlanner executionPlanner) {
      this.reporter = reporter;
      this.executionPlanner = executionPlanner;
    }

    public void execute(Definitions definitions, List<String> names) {
      reporter.startNewPhase("Generating tree");
      FindCallables.findCallables(reporter, definitions, names)
          .ifPresent(functions -> functions.forEach(f -> print(treeOf(f))));
    }

    private Task treeOf(Callable callable) {
      return executionPlanner.createPlan(
          callable.createAgrlessCallExpression(commandLineLocation()));
    }

    private void print(Task task) {
      print("", task);
    }

    private void print(String indent, Task task) {
      reporter.printlnRaw(indent + task.description());
      task.dependencies().forEach(ch -> print(indent + "  ", ch));
    }
  }
}

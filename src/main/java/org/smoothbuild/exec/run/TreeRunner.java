package org.smoothbuild.exec.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.exec.run.FindFunctions.findFunctions;
import static org.smoothbuild.exec.run.ValidateFunctionNames.validateFunctionNames;
import static org.smoothbuild.lang.base.Location.commandLineLocation;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.parse.RuntimeController;

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
    List<String> errors = validateFunctionNames(names);
    if (!errors.isEmpty()) {
      console.errors(errors);
      return EXIT_CODE_ERROR;
    }
    return runtimeController.setUpRuntimeAndRun((runtime) -> treeExecutor.execute(runtime, names));
  }

  public static class TreeExecutor {
    private final Reporter reporter;

    @Inject
    public TreeExecutor(Reporter reporter) {
      this.reporter = reporter;
    }

    public void execute(SRuntime runtime, List<String> names) {
      reporter.startNewPhase("Generating tree");
      findFunctions(reporter, runtime, names)
          .ifPresent(functions -> functions.forEach(f -> print(treeOf(f))));
    }

    private Task treeOf(Function function) {
      return function
          .createAgrlessCallExpression(commandLineLocation())
          .createTask(null);
    }

    private void print(Task task) {
      print("", task);
    }

    private void print(String indent, Task task) {
      reporter.printlnRaw(indent + task.description());
      task.children().forEach(ch -> print(indent + "  ", ch));
    }
  }
}

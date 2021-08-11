package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.run.FindReferencables.findReferencables;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.plan.ExecutionPlanner;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Value;

public class PlanRunner {
  private final Console console;
  private final RuntimeController runtimeController;
  private final PlanExecutor planExecutor;

  @Inject
  public PlanRunner(Console console, RuntimeController runtimeController,
      PlanExecutor planExecutor) {
    this.console = console;
    this.runtimeController = runtimeController;
    this.planExecutor = planExecutor;
  }

  public int run(List<String> names) {
    List<String> errors = ValidateValueNames.validateValueNames(names);
    if (!errors.isEmpty()) {
      console.errors(errors);
      return EXIT_CODE_ERROR;
    }
    return runtimeController.setUpRuntimeAndRun(
        (definitions) -> planExecutor.execute(definitions, names));
  }

  public static class PlanExecutor {
    private final Reporter reporter;
    private final ExecutionPlanner executionPlanner;

    @Inject
    public PlanExecutor(Reporter reporter, ExecutionPlanner executionPlanner) {
      this.reporter = reporter;
      this.executionPlanner = executionPlanner;
    }

    public void execute(Definitions definitions, List<String> names) {
      reporter.startNewPhase("Creating execution plan");
      findReferencables(reporter, definitions, names)
          .ifPresent(values -> printPlans(definitions, values));
    }

    private void printPlans(Definitions definitions, List<Value> values) {
      executionPlanner.createPlans(definitions, values)
          .values()
          .forEach(this::print);
    }

    private void print(Task task) {
      print("", task);
    }

    private void print(String indent, Task task) {
      reporter.printlnRaw(indent + task.description());
      task.dependencies().forEach(d -> print(indent + "  ", d));
    }
  }
}

package org.smoothbuild.exec.run;

import static org.smoothbuild.exec.run.ValidateFunctionArguments.validateFunctionArguments;
import static org.smoothbuild.lang.base.Location.commandLineLocation;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.runtime.SRuntime;

public class TreeRunner {
  private final Reporter reporter;

  @Inject
  public TreeRunner(Reporter reporter) {
    this.reporter = reporter;
  }

  public void execute(SRuntime runtime, List<String> names) {
    reporter.startNewPhase("Generating tree");
    List<Function> functionsToRun = validateFunctionArguments(reporter, runtime, names);
    if (!reporter.isProblemReported()) {
      functionsToRun.forEach(f -> print(treeOf(f)));
    }
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

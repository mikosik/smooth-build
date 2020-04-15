package org.smoothbuild.exec.run;

import static org.smoothbuild.exec.run.ValidateFunctionArguments.validateFunctionArguments;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.runtime.SRuntime;

public class TreeRunner {
  private final Console console;

  @Inject
  public TreeRunner(Console console) {
    this.console = console;
  }

  public void execute(SRuntime runtime, Set<String> names) {
    console.println("Generating tree");
    List<Function> functionsToRun = validateFunctionArguments(console, runtime, names);
    if (!console.isProblemReported()) {
      functionsToRun.forEach(f -> print(treeOf(f)));
    }
  }

  private Task treeOf(Function function) {
    return function
        .createAgrlessCallExpression()
        .createTask(null);
  }

  private void print(Task task) {
    print("", task);
  }

  private void print(String indent, Task task) {
    console.println(indent + task.description());
    task.children().forEach(ch -> print(indent + "  ", ch));
  }
}

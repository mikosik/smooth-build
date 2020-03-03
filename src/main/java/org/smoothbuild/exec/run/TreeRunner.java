package org.smoothbuild.exec.run;

import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.exec.task.Task;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.SRuntime;

public class TreeRunner {
  private final Console console;

  @Inject
  public TreeRunner(Console console) {
    this.console = console;
  }

  public void execute(SRuntime runtime, Set<String> functionNames) {
    Functions functions = runtime.functions();
    for (String name : functionNames) {
      if (!functions.contains(name)) {
        console.error("Unknown function '" + name + "'.\n"
            + "Use 'smooth list' to see all available functions.\n");
        return;
      }
      Function function = functions.get(name);
      if (!function.canBeCalledArgless()) {
        console.error(
            "Cannot print execution tree for '" + name + "' function as it requires arguments.");
        return;
      }
    }
    functionNames.forEach(n -> print(treeOf(runtime.functions().get(n))));
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
    console.println(indent + task.type().name() + " " + task.name());
    task.children().forEach(ch -> print(indent + "  ", ch));
  }
}

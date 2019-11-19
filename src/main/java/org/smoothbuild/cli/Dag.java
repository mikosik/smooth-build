package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.ArgumentValidator.validateFunctionNames;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.parse.RuntimeController;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableList;

public class Dag implements Command {
  private final ObjectsDb objectsDb;
  private final Console console;
  private final RuntimeController runtimeController;

  @Inject
  public Dag(ObjectsDb objectsDb, Console console, RuntimeController runtimeController) {
    this.objectsDb = objectsDb;
    this.console = console;
    this.runtimeController = runtimeController;
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
        (runtime) -> functionNames.value()
            .forEach(name -> print(dagOf(runtime.functions().get(name)))));
  }

  private Task dagOf(Function function) {
    return function
        .createCallExpression(list(), unknownLocation())
        .createTask(objectsDb, null);
  }

  private void print(Task task) {
    print("", task);
  }

  private void print(String indent, Task task) {
    console.println(indent + task.name() + "(" + task.type().name() + ")");
    task.dependencies().forEach(ch -> print(indent + "  ", ch));
  }
}

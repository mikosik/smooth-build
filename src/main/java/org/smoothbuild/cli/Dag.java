package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.ArgumentValidator.validateFunctionNames;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.parse.RuntimeController;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableList;

public class Dag implements Command {
  private final ValuesDb valuesDb;
  private final Console console;
  private final RuntimeController runtimeController;

  @Inject
  public Dag(ValuesDb valuesDb, Console console, RuntimeController runtimeController) {
    this.valuesDb = valuesDb;
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
            .forEach(name -> print(dagEvaluator(runtime.functions().get(name)))));
  }

  private Evaluator dagEvaluator(Function function) {
    return function
        .createCallExpression(list(), unknownLocation())
        .createEvaluator(valuesDb, null);
  }

  private void print(Evaluator dag) {
    print("", dag);
  }

  private void print(String indent, Evaluator dag) {
    console.println(indent + dag.name() + "(" + dag.type().name() + ")");
    dag.children().forEach(ch -> print(indent + "  ", ch));
  }
}

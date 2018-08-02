package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.ArgumentValidator.validateFunctionNames;
import static org.smoothbuild.lang.base.Location.unknownLocation;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.expr.Expression;
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
      console.rawErrors(functionNames.errors());
      return EXIT_CODE_ERROR;
    }
    return runtimeController.setUpRuntimeAndRun((runtime) -> {
      functionNames.value().stream()
          .forEach(name -> print(dagEvaluator(runtime.functions().get(name))));
    });
  }

  private org.smoothbuild.util.Dag<Evaluator> dagEvaluator(Function function) {
    org.smoothbuild.util.Dag<Expression> expression =
        new org.smoothbuild.util.Dag<>(function.createCallExpression(unknownLocation()));
    return expression.elem().createEvaluator(expression.children(), valuesDb, null);
  }

  private void print(org.smoothbuild.util.Dag<Evaluator> dag) {
    print("", dag);
  }

  private void print(String indent, org.smoothbuild.util.Dag<Evaluator> dag) {
    Evaluator elem = dag.elem();
    console.println(indent + elem.name() + "(" + elem.type().name() + ")");
    dag
        .children()
        .stream()
        .forEach(ch -> print(indent + "  ", ch));
  }
}

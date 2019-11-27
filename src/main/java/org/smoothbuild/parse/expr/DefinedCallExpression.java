package org.smoothbuild.parse.expr;

import static org.smoothbuild.exec.task.Task.taskTypes;
import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.lang.object.type.GenericTypeMap.inferMapping;

import java.util.List;

import org.smoothbuild.exec.comp.Computation;
import org.smoothbuild.exec.comp.IdentityComputation;
import org.smoothbuild.exec.task.Task;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.type.ConcreteType;

import com.google.common.collect.ImmutableList;

public class DefinedCallExpression extends Expression {
  private final DefinedFunction function;

  public DefinedCallExpression(DefinedFunction definedFunction,
      List<? extends Expression> arguments, Location location) {
    super(arguments, location);
    this.function = definedFunction;
  }

  @Override
  public Task createTask(Scope<Task> scope) {
    List<Task> arguments = childrenTasks(scope);
    ConcreteType actualResultType =
        inferMapping(function.parameterTypes(), taskTypes(arguments))
            .applyTo(function.signature().type());
    Task task = function
        .body()
        .createTask(functionScope(arguments))
        .convertIfNeeded(actualResultType);

    Computation computation = new IdentityComputation(actualResultType);
    List<Task> dependencies = ImmutableList.of(task);
    return new Task(computation, function.name(), true, dependencies, location());
  }

  private Scope<Task> functionScope(List<Task> arguments) {
    Scope<Task> functionScope = scope();
    for (int i = 0; i < arguments.size(); i++) {
      functionScope.add(function.parameters().get(i).name(), arguments.get(i));
    }
    return functionScope;
  }
}

package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.lang.object.type.GenericTypeMap.inferMapping;
import static org.smoothbuild.task.base.Task.taskTypes;

import java.util.List;

import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.task.base.Computation;
import org.smoothbuild.task.base.IdentityComputation;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;

public class DefinedCallExpression extends Expression {
  private final DefinedFunction function;

  public DefinedCallExpression(DefinedFunction definedFunction,
      List<? extends Expression> arguments, Location location) {
    super(arguments, location);
    this.function = definedFunction;
  }

  @Override
  public Task createTask(ObjectsDb objectsDb, Scope<Task> scope) {
    List<Task> arguments = childrenTasks(objectsDb, scope);
    ConcreteType actualResultType =
        inferMapping(function.parameterTypes(), taskTypes(arguments))
            .applyTo(function.signature().type());
    Task task = function
        .body()
        .createTask(objectsDb, functionScope(arguments))
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

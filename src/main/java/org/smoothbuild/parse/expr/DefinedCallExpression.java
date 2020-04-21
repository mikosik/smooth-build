package org.smoothbuild.parse.expr;

import static org.smoothbuild.exec.task.base.BuildTask.taskTypes;
import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.lang.object.type.GenericTypeMap.inferMapping;

import java.util.List;

import org.smoothbuild.exec.task.base.BuildTask;
import org.smoothbuild.exec.task.base.VirtualTask;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.type.ConcreteType;

public class DefinedCallExpression extends Expression {
  private final DefinedFunction function;

  public DefinedCallExpression(DefinedFunction definedFunction,
      List<? extends Expression> arguments, Location location) {
    super(arguments, location);
    this.function = definedFunction;
  }

  @Override
  public BuildTask createTask(Scope<BuildTask> scope) {
    List<BuildTask> arguments = childrenTasks(scope);
    ConcreteType actualResultType =
        inferMapping(function.parameterTypes(), taskTypes(arguments))
            .applyTo(function.signature().type());
    BuildTask task = function
        .body()
        .createTask(functionScope(arguments))
        .convertIfNeeded(actualResultType);

    return new VirtualTask(function.name(), actualResultType, task, location());
  }

  private Scope<BuildTask> functionScope(List<BuildTask> arguments) {
    Scope<BuildTask> functionScope = scope();
    for (int i = 0; i < arguments.size(); i++) {
      functionScope.add(function.parameters().get(i).name(), arguments.get(i));
    }
    return functionScope;
  }
}

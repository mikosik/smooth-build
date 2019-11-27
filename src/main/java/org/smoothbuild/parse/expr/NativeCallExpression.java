package org.smoothbuild.parse.expr;

import static org.smoothbuild.exec.task.Task.taskTypes;
import static org.smoothbuild.lang.object.type.GenericTypeMap.inferMapping;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.exec.comp.Computation;
import org.smoothbuild.exec.comp.NativeCallComputation;
import org.smoothbuild.exec.task.Task;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.GenericTypeMap;
import org.smoothbuild.lang.object.type.Type;

public class NativeCallExpression extends Expression {
  private final NativeFunction nativeFunction;

  public NativeCallExpression(NativeFunction nativeFunction, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.nativeFunction = nativeFunction;
  }

  @Override
  public Task createTask(Scope<Task> scope) {
    List<Task> arguments = childrenTasks(scope);
    List<Type> parameterTypes = nativeFunction.parameterTypes();
    GenericTypeMap<ConcreteType> mapping = inferMapping(parameterTypes, taskTypes(arguments));
    ConcreteType actualResultType = mapping.applyTo(nativeFunction.signature().type());

    Computation computation = new NativeCallComputation(actualResultType, nativeFunction);
    List<Task> dependencies = convertedArguments(mapping.applyTo(parameterTypes), arguments);
    return new Task(
        computation, nativeFunction.name(), nativeFunction.isCacheable(), dependencies, location());
  }

  private static List<Task> convertedArguments(
      List<ConcreteType> actualParameterTypes, List<Task> arguments) {
    List<Task> result = new ArrayList<>();
    for (int i = 0; i < arguments.size(); i++) {
      result.add(arguments.get(i).convertIfNeeded(actualParameterTypes.get(i)));
    }
    return result;
  }
}

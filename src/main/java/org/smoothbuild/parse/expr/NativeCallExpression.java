package org.smoothbuild.parse.expr;

import static org.smoothbuild.exec.task.base.Task.taskTypes;
import static org.smoothbuild.lang.object.type.GenericTypeMap.inferMapping;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.NativeCallAlgorithm;
import org.smoothbuild.exec.task.base.Task;
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

    Algorithm algorithm = new NativeCallAlgorithm(actualResultType, nativeFunction);
    List<Task> dependencies = convertedArguments(mapping.applyTo(parameterTypes), arguments);
    return new Task(algorithm, dependencies, location(), nativeFunction.isCacheable());
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

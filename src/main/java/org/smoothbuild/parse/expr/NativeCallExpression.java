package org.smoothbuild.parse.expr;

import static org.smoothbuild.exec.task.base.BuildTask.taskTypes;
import static org.smoothbuild.lang.object.type.GenericTypeMap.inferMapping;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.NativeCallAlgorithm;
import org.smoothbuild.exec.task.base.BuildTask;
import org.smoothbuild.exec.task.base.IfTask;
import org.smoothbuild.exec.task.base.NormalTask;
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
  public BuildTask createTask(Scope<BuildTask> scope) {
    List<BuildTask> arguments = childrenTasks(scope);
    List<Type> parameterTypes = nativeFunction.parameterTypes();
    GenericTypeMap<ConcreteType> mapping = inferMapping(parameterTypes, taskTypes(arguments));
    ConcreteType actualResultType = mapping.applyTo(nativeFunction.signature().type());

    Algorithm algorithm = new NativeCallAlgorithm(actualResultType, nativeFunction);
    List<BuildTask> dependencies = convertedArguments(mapping.applyTo(parameterTypes), arguments);
    if (nativeFunction.name().equals("if")) {
      return new IfTask(algorithm, dependencies, location(), nativeFunction.isCacheable());
    } else {
      return new NormalTask(algorithm, dependencies, location(), nativeFunction.isCacheable());
    }
  }

  private static List<BuildTask> convertedArguments(
      List<ConcreteType> actualParameterTypes, List<BuildTask> arguments) {
    List<BuildTask> result = new ArrayList<>();
    for (int i = 0; i < arguments.size(); i++) {
      result.add(arguments.get(i).convertIfNeeded(actualParameterTypes.get(i)));
    }
    return result;
  }
}

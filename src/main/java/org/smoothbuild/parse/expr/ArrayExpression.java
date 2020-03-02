package org.smoothbuild.parse.expr;

import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.exec.comp.ArrayComputation;
import org.smoothbuild.exec.comp.Computation;
import org.smoothbuild.exec.task.Task;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.type.ArrayType;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.Type;

public class ArrayExpression extends Expression {
  private final ArrayType arrayType;

  public ArrayExpression(ArrayType arrayType, List<? extends Expression> elements,
      Location location) {
    super(elements, location);
    this.arrayType = arrayType;
  }

  @Override
  public Task createTask(Scope<Task> scope) {
    List<Task> elements = childrenTasks(scope);
    ConcreteArrayType actualType = arrayType(elements);

    Computation computation = new ArrayComputation(actualType);
    List<Task> convertedElements = convertedElements(actualType.elemType(), elements);
    return new Task(computation, true, convertedElements, location());
  }

  private static List<Task> convertedElements(ConcreteType type, List<Task> elements) {
    return map(elements, t -> t.convertIfNeeded(type));
  }

  private ConcreteArrayType arrayType(List<Task> elements) {
    return (ConcreteArrayType) elements
        .stream()
        .map(t -> (Type) t.type())
        .reduce(Type::commonSuperType)
        .map(t -> t.changeCoreDepthBy(1))
        .orElse(arrayType);
  }
}

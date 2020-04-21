package org.smoothbuild.parse.expr;

import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ArrayLiteralAlgorithm;
import org.smoothbuild.exec.task.base.BuildTask;
import org.smoothbuild.exec.task.base.NormalTask;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.type.ArrayType;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.Type;

public class ArrayLiteralExpression extends Expression {
  private final ArrayType arrayType;

  public ArrayLiteralExpression(ArrayType arrayType, List<? extends Expression> elements,
      Location location) {
    super(elements, location);
    this.arrayType = arrayType;
  }

  @Override
  public BuildTask createTask(Scope<BuildTask> scope) {
    List<BuildTask> elements = childrenTasks(scope);
    ConcreteArrayType actualType = arrayType(elements);

    Algorithm algorithm = new ArrayLiteralAlgorithm(actualType);
    List<BuildTask> convertedElements = convertedElements(actualType.elemType(), elements);
    return new NormalTask(algorithm, convertedElements, location(), true);
  }

  private static List<BuildTask> convertedElements(ConcreteType type, List<BuildTask> elements) {
    return map(elements, t -> t.convertIfNeeded(type));
  }

  private ConcreteArrayType arrayType(List<BuildTask> elements) {
    return (ConcreteArrayType) elements
        .stream()
        .map(t -> (Type) t.type())
        .reduce(Type::commonSuperType)
        .map(t -> t.changeCoreDepthBy(1))
        .orElse(arrayType);
  }
}

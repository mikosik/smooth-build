package org.smoothbuild.exec.task.plan;

import static org.smoothbuild.exec.task.base.IfTask.IF_FUNCTION_NAME;
import static org.smoothbuild.exec.task.base.Task.taskTypes;
import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.lang.base.type.GenericTypeMap.inferMapping;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.exec.comp.AccessorCallAlgorithm;
import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ArrayLiteralAlgorithm;
import org.smoothbuild.exec.comp.ConstructorCallAlgorithm;
import org.smoothbuild.exec.comp.ConvertAlgorithm;
import org.smoothbuild.exec.comp.NativeCallAlgorithm;
import org.smoothbuild.exec.comp.StringLiteralAlgorithm;
import org.smoothbuild.exec.task.base.IfTask;
import org.smoothbuild.exec.task.base.NormalTask;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.exec.task.base.VirtualTask;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.base.type.ConcreteArrayType;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.base.type.GenericTypeMap;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.parse.ast.Named;
import org.smoothbuild.parse.expr.AccessorCallExpression;
import org.smoothbuild.parse.expr.ArrayLiteralExpression;
import org.smoothbuild.parse.expr.BoundValueExpression;
import org.smoothbuild.parse.expr.ConstructorCallExpression;
import org.smoothbuild.parse.expr.DefinedCallExpression;
import org.smoothbuild.parse.expr.Expression;
import org.smoothbuild.parse.expr.ExpressionVisitor;
import org.smoothbuild.parse.expr.NativeCallExpression;
import org.smoothbuild.parse.expr.StringLiteralExpression;

import com.google.common.collect.ImmutableList;

public class ExpressionToTaskConverter extends ExpressionVisitor<Task> {
  private final TypeToBinaryTypeConverter typeConverter;
  private Scope<Task> scope;

  @Inject
  public ExpressionToTaskConverter(ObjectFactory objectFactory) {
    this.typeConverter = new TypeToBinaryTypeConverter(objectFactory);
    this.scope = scope();
  }

  @Override
  public Task visit(AccessorCallExpression expression) {
    Accessor accessor = expression.accessor();
    ConcreteType type = accessor.type();
    Algorithm algorithm = new AccessorCallAlgorithm(accessor, type.visit(typeConverter));
    List<Task> children = childrenTasks(expression.children());
    return new NormalTask(
        type, "." + accessor.name(), algorithm, children, accessor.location(), true);
  }

  @Override
  public Task visit(ArrayLiteralExpression expression) {
    List<Task> elements = childrenTasks(expression.children());
    ConcreteArrayType actualType = arrayType(elements, (Type) expression.arrayType());

    Algorithm algorithm = new ArrayLiteralAlgorithm(typeConverter.visit(actualType));
    List<Task> convertedElements = convertedElements(actualType.elemType(), elements);
    return new NormalTask(
        actualType, actualType.name(), algorithm, convertedElements, expression.location(), true);
  }

  private List<Task> convertedElements(ConcreteType type, List<Task> elements) {
    return map(elements, t -> convertIfNeeded(t, type));
  }

  private ConcreteArrayType arrayType(List<Task> elements, Type arrayType) {
    return (ConcreteArrayType) elements
        .stream()
        .map(t -> (Type) t.type())
        .reduce((type, type2) -> type.commonSuperType(type2).get())
        .map(t -> t.changeCoreDepthBy(1))
        .orElse(arrayType);
  }

  @Override
  public Task visit(BoundValueExpression expression) {
    return scope.get(expression.name());
  }

  @Override
  public Task visit(ConstructorCallExpression expression) {
    Constructor constructor = expression.constructor();
    StructType type = typeConverter.visit(constructor.type());
    Algorithm algorithm = new ConstructorCallAlgorithm(type);
    List<Task> dependencies = childrenTasks(expression.children());
    return new NormalTask(constructor.type(), constructor.name(), algorithm, dependencies,
        expression.location(), true);
  }

  @Override
  public Task visit(DefinedCallExpression expression) {
    List<Task> arguments = childrenTasks(expression.children());
    DefinedFunction function = expression.function();
    ConcreteType actualResultType =
        inferMapping(function.parameterTypes(), taskTypes(arguments))
            .applyTo(function.signature().type());

    scope = scope(scope);
    addArgumentsToScope(scope, function.parameters(), arguments);
    Task definedCallTask = function.body().visit(this);
    scope = scope.outerScope();

    Task task = convertIfNeeded(definedCallTask, actualResultType);
    return new VirtualTask(function.name(), actualResultType, task, expression.location());
  }

  private static void addArgumentsToScope(Scope<Task> scope, List<? extends Named> names,
      List<Task> arguments) {
    for (int i = 0; i < arguments.size(); i++) {
      scope.add(names.get(i).name(), arguments.get(i));
    }
  }

  @Override
  public Task visit(NativeCallExpression expression) {
    List<Task> arguments = childrenTasks(expression.children());
    NativeFunction nativeFunction = expression.nativeFunction();
    List<Type> parameterTypes = nativeFunction.parameterTypes();
    GenericTypeMap<ConcreteType> mapping = inferMapping(parameterTypes, taskTypes(arguments));
    ConcreteType actualResultType = mapping.applyTo(nativeFunction.signature().type());

    Algorithm algorithm = new NativeCallAlgorithm(
        actualResultType.visit(typeConverter), nativeFunction);
    List<Task> dependencies = convertedArguments(mapping.applyTo(parameterTypes), arguments);
    if (nativeFunction.name().equals(IF_FUNCTION_NAME)) {
      return new IfTask(actualResultType, algorithm, dependencies, expression.location(),
          nativeFunction.isCacheable());
    } else {
      return new NormalTask(actualResultType, nativeFunction.name(), algorithm, dependencies,
          expression.location(), nativeFunction.isCacheable());
    }
  }

  private List<Task> convertedArguments(List<ConcreteType> actualParameterTypes,
      List<Task> arguments) {
    List<Task> result = new ArrayList<>();
    for (int i = 0; i < arguments.size(); i++) {
      ConcreteType concreteType = actualParameterTypes.get(i);
      result.add(convertIfNeeded(arguments.get(i), concreteType));
    }
    return result;
  }

  @Override
  public Task visit(StringLiteralExpression expression) {
    var stringType = typeConverter.visit(string());
    var algorithm = new StringLiteralAlgorithm(stringType, expression.string());
    return new NormalTask(string(), algorithm.shortedString(), algorithm, ImmutableList.of(),
        expression.location(), true);
  }

  public List<Task> childrenTasks(List<Expression> children) {
    return map(children, ch -> ch.visit(this));
  }

  public Task convertIfNeeded(Task task, ConcreteType requiredType) {
    if (task.type().equals(requiredType)) {
      return task;
    } else {
      String description = requiredType.name() + "<-" + task.type().name();
      Algorithm algorithm = new ConvertAlgorithm(requiredType.visit(typeConverter));
      List<Task> dependencies = list(task);
      return new NormalTask(
          requiredType, description, algorithm, dependencies, task.location(), true);
    }
  }
}

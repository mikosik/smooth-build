package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.compute.IfTask.IF_FUNCTION_NAME;
import static org.smoothbuild.exec.compute.Task.taskTypes;
import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.exec.compute.TaskKind.CONVERSION;
import static org.smoothbuild.exec.compute.TaskKind.LITERAL;
import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.lang.base.type.GenericTypeMap.inferMapping;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.algorithm.CallNativeAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.CreateArrayAlgorithm;
import org.smoothbuild.exec.algorithm.CreateTupleAlgorithm;
import org.smoothbuild.exec.algorithm.FixedBlobAlgorithm;
import org.smoothbuild.exec.algorithm.FixedStringAlgorithm;
import org.smoothbuild.exec.algorithm.ReadTupleElementAlgorithm;
import org.smoothbuild.exec.compute.IfTask;
import org.smoothbuild.exec.compute.NormalTask;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.compute.VirtualTask;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.base.type.ConcreteArrayType;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.base.type.GenericTypeMap;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.AccessorCallExpression;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.BoundValueExpression;
import org.smoothbuild.lang.expr.ConstructorCallExpression;
import org.smoothbuild.lang.expr.DefinedCallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ExpressionVisitor;
import org.smoothbuild.lang.expr.NativeCallExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.parse.ast.Named;
import org.smoothbuild.record.db.RecordFactory;
import org.smoothbuild.record.spec.TupleSpec;

import com.google.common.collect.ImmutableList;

public class ExpressionToTaskConverter extends ExpressionVisitor<Task> {
  private final TypeToBinaryTypeConverter typeConverter;
  private Scope<Task> scope;

  @Inject
  public ExpressionToTaskConverter(RecordFactory recordFactory) {
    this.typeConverter = new TypeToBinaryTypeConverter(recordFactory);
    this.scope = scope();
  }

  @Override
  public Task visit(AccessorCallExpression expression) {
    Accessor accessor = expression.accessor();
    ConcreteType type = accessor.type();
    Algorithm algorithm = new ReadTupleElementAlgorithm(accessor, type.visit(typeConverter));
    List<Task> children = childrenTasks(expression.children());
    return new NormalTask(
        CALL, type, "." + accessor.name(), algorithm, children, accessor.location(), true);
  }

  @Override
  public Task visit(ArrayLiteralExpression expression) {
    List<Task> elements = childrenTasks(expression.children());
    ConcreteArrayType actualType = arrayType(elements, (Type) expression.arrayType());

    Algorithm algorithm = new CreateArrayAlgorithm(typeConverter.visit(actualType));
    List<Task> convertedElements = convertedElements(actualType.elemType(), elements);
    return new NormalTask(LITERAL, actualType, actualType.name(), algorithm, convertedElements,
        expression.location(), true);
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
    TupleSpec type = typeConverter.visit(constructor.type());
    Algorithm algorithm = new CreateTupleAlgorithm(type);
    List<Task> dependencies = childrenTasks(expression.children());
    return new NormalTask(CALL, constructor.type(), constructor.name(), algorithm, dependencies,
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

    Algorithm algorithm = new CallNativeAlgorithm(
        actualResultType.visit(typeConverter), nativeFunction);
    List<Task> dependencies = convertedArguments(mapping.applyTo(parameterTypes), arguments);
    if (nativeFunction.name().equals(IF_FUNCTION_NAME)) {
      return new IfTask(actualResultType, algorithm, dependencies, expression.location(),
          nativeFunction.isCacheable());
    } else {
      return new NormalTask(CALL, actualResultType, nativeFunction.name(), algorithm, dependencies,
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
    var algorithm = new FixedStringAlgorithm(stringType, expression.string());
    return new NormalTask(LITERAL, string(), algorithm.shortedString(), algorithm,
        ImmutableList.of(), expression.location(), true);
  }

  @Override
  public Task visit(BlobLiteralExpression expression) {
    var blobSpec = typeConverter.visit(blob());
    var algorithm = new FixedBlobAlgorithm(blobSpec, expression.byteString());
    return new NormalTask(LITERAL, blob(), algorithm.shortedLiteral(), algorithm,
        ImmutableList.of(), expression.location(), true);
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
          CONVERSION, requiredType, description, algorithm, dependencies, task.location(), true);
    }
  }
}

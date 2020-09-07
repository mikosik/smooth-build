package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.compute.IfTask.IF_FUNCTION_NAME;
import static org.smoothbuild.exec.compute.Task.taskTypes;
import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.exec.compute.TaskKind.CONVERSION;
import static org.smoothbuild.exec.compute.TaskKind.LITERAL;
import static org.smoothbuild.exec.compute.TaskKind.VALUE;
import static org.smoothbuild.lang.base.type.GenericTypeMap.inferMapping;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.algorithm.CallNativeAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.CreateArrayAlgorithm;
import org.smoothbuild.exec.algorithm.CreateTupleAlgorithm;
import org.smoothbuild.exec.algorithm.FixedBlobAlgorithm;
import org.smoothbuild.exec.algorithm.FixedStringAlgorithm;
import org.smoothbuild.exec.algorithm.ReadTupleElementAlgorithm;
import org.smoothbuild.exec.compute.ComputableTask;
import org.smoothbuild.exec.compute.IfTask;
import org.smoothbuild.exec.compute.NormalTask;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.compute.VirtualTask;
import org.smoothbuild.exec.nativ.LoadingNativeImplException;
import org.smoothbuild.exec.nativ.Native;
import org.smoothbuild.exec.nativ.NativeImplLoader;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.base.type.ConcreteArrayType;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.base.type.GenericTypeMap;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.ConstructorCallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ExpressionVisitor;
import org.smoothbuild.lang.expr.ExpressionVisitorException;
import org.smoothbuild.lang.expr.FieldReadExpression;
import org.smoothbuild.lang.expr.FunctionCallExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.expr.ValueReferenceExpression;
import org.smoothbuild.lang.parse.Definitions;
import org.smoothbuild.lang.parse.ast.Named;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ExpressionToTaskConverter extends ExpressionVisitor<Task> {
  private final Definitions definitions;
  private final TypeToSpecConverter toSpecConverter;
  private final NativeImplLoader nativeImplLoader;
  private Scope<Task> scope;

  @Inject
  public ExpressionToTaskConverter(Definitions definitions, ObjectFactory objectFactory,
      NativeImplLoader nativeImplLoader) {
    this.toSpecConverter = new TypeToSpecConverter(objectFactory);
    this.definitions = definitions;
    this.scope = new Scope<>(Map.of());
    this.nativeImplLoader = nativeImplLoader;
  }

  @Override
  public Task visit(FieldReadExpression expression) throws ExpressionVisitorException {
    Field field = expression.field();
    Algorithm algorithm = new ReadTupleElementAlgorithm(
        field.index(), field.type().visit(toSpecConverter));
    List<Task> children = childrenTasks(expression.children());
    return new NormalTask(
        CALL, field.type(), "." + field.name(), algorithm, children, expression.location(), true);
  }

  @Override
  public Task visit(ArrayLiteralExpression expression) throws ExpressionVisitorException {
    List<Task> elements = childrenTasks(expression.children());
    ConcreteArrayType actualType = arrayType(elements, (Type) expression.arrayType());

    Algorithm algorithm = new CreateArrayAlgorithm(toSpecConverter.visit(actualType));
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
  public Task visit(ValueReferenceExpression expression) throws ExpressionVisitorException {
    Value value = (Value) definitions.evaluables().get(expression.name());
    if (value.body().isPresent()) {
      Task task = value.body().get().visit(this);
      Task convertedTask = convertIfNeeded(task, value.type());
      return new VirtualTask(value.extendedName(), convertedTask, VALUE, expression.location());
    } else {
      Native nativ = loadNative(value);
      Algorithm algorithm = new CallNativeAlgorithm(value.type().visit(toSpecConverter), nativ);
      return new NormalTask(VALUE, value.type(), value.extendedName(), algorithm,
          ImmutableList.of(), expression.location(), nativ.cacheable());
    }
  }

  private Native loadNative(Value value) throws ExpressionVisitorException {
    try {
      return nativeImplLoader.loadNative(value);
    } catch (LoadingNativeImplException e) {
      throw new ExpressionVisitorException(e.getMessage(), e);
    }
  }

  @Override
  public Task visit(ParameterReferenceExpression expression) {
    return scope.get(expression.name());
  }

  @Override
  public Task visit(ConstructorCallExpression expression) throws ExpressionVisitorException {
    Constructor constructor = expression.constructor();
    TupleSpec type = toSpecConverter.visit(constructor.type());
    Algorithm algorithm = new CreateTupleAlgorithm(type);
    List<Task> dependencies = childrenTasks(expression.children());
    return new NormalTask(CALL, constructor.type(), constructor.extendedName(), algorithm,
        dependencies, expression.location(), true);
  }

  @Override
  public Task visit(FunctionCallExpression expression) throws ExpressionVisitorException {
    List<Task> arguments = childrenTasks(expression.children());
    Function function = expression.function();
    GenericTypeMap<ConcreteType> mapping =
        inferMapping(function.parameterTypes(), taskTypes(arguments));
    ConcreteType actualResultType = mapping.applyTo(function.signature().type());

    if (function.body().isPresent()) {
      return taskForDefinedFunction(actualResultType, function, arguments, expression.location());
    } else {
      return taskForNativeFunction(arguments, function, mapping, actualResultType,
          expression.location());
    }
  }

  private VirtualTask taskForDefinedFunction(ConcreteType actualResultType, Function function,
      List<Task> arguments, Location location) throws ExpressionVisitorException {
    scope = new Scope<>(scope, nameToArgumentMap(function.parameters(), arguments));
    Task definedCallTask = function.body().get().visit(this);
    scope = scope.outerScope();
    Task task = convertIfNeeded(definedCallTask, actualResultType);
    return new VirtualTask(function.extendedName(), task, CALL, location);
  }

  private ComputableTask taskForNativeFunction(List<Task> arguments, Function function,
      GenericTypeMap<ConcreteType> mapping, ConcreteType actualResultType, Location location)
      throws ExpressionVisitorException {
    Native nativ = loadNative(function);
    Algorithm algorithm = new CallNativeAlgorithm(actualResultType.visit(toSpecConverter), nativ);
    List<Task> dependencies =
        convertedArguments(mapping.applyTo(function.parameterTypes()), arguments);
    if (function.name().equals(IF_FUNCTION_NAME)) {
      return new IfTask(actualResultType, algorithm, dependencies, location, nativ.cacheable());
    } else {
      return new NormalTask(CALL, actualResultType, function.extendedName(), algorithm,
          dependencies, location, nativ.cacheable());
    }
  }

  private static ImmutableMap<String, Task> nameToArgumentMap(List<? extends Named> names,
      List<Task> arguments) {
    Builder<String, Task> builder = ImmutableMap.builder();
    for (int i = 0; i < arguments.size(); i++) {
      builder.put(names.get(i).name(), arguments.get(i));
    }
    return builder.build();
  }

  private Native loadNative(Function function) throws ExpressionVisitorException {
    try {
      return nativeImplLoader.loadNative(function);
    } catch (LoadingNativeImplException e) {
      throw new ExpressionVisitorException(e.getMessage(), e);
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
    var stringType = toSpecConverter.visit(string());
    var algorithm = new FixedStringAlgorithm(stringType, expression.string());
    return new NormalTask(LITERAL, string(), algorithm.shortedString(), algorithm,
        ImmutableList.of(), expression.location(), true);
  }

  @Override
  public Task visit(BlobLiteralExpression expression) {
    var blobSpec = toSpecConverter.visit(blob());
    var algorithm = new FixedBlobAlgorithm(blobSpec, expression.byteString());
    return new NormalTask(LITERAL, blob(), algorithm.shortedLiteral(), algorithm,
        ImmutableList.of(), expression.location(), true);
  }

  private List<Task> childrenTasks(List<Expression> children) throws ExpressionVisitorException {
    ImmutableList.Builder<Task> builder = ImmutableList.builder();
    for (Expression child : children) {
      builder.add(child.visit(this));
    }
    return builder.build();
  }

  private Task convertIfNeeded(Task task, ConcreteType requiredType) {
    if (task.type().equals(requiredType)) {
      return task;
    } else {
      return convert(requiredType, task);
    }
  }

  private NormalTask convert(ConcreteType requiredType, Task task) {
    String description = requiredType.name() + "<-" + task.type().name();
    Algorithm algorithm = new ConvertAlgorithm(requiredType.visit(toSpecConverter));
    List<Task> dependencies = list(task);
    return new NormalTask(
        CONVERSION, requiredType, description, algorithm, dependencies, task.location(), true);
  }
}

package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.compute.IfTask.IF_FUNCTION_NAME;
import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.exec.compute.TaskKind.CONVERSION;
import static org.smoothbuild.exec.compute.TaskKind.LITERAL;
import static org.smoothbuild.exec.compute.TaskKind.VALUE;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.Type.inferVariableBounds;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.zip;

import java.util.ArrayList;
import java.util.List;

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
import org.smoothbuild.exec.compute.IfTask;
import org.smoothbuild.exec.compute.NormalTask;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.compute.VirtualTask;
import org.smoothbuild.exec.nativ.LoadingNativeImplException;
import org.smoothbuild.exec.nativ.Native;
import org.smoothbuild.exec.nativ.NativeImplLoader;
import org.smoothbuild.lang.base.define.Callable;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Scope;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.BoundedVariables;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ExpressionVisitor;
import org.smoothbuild.lang.expr.ExpressionVisitorException;
import org.smoothbuild.lang.expr.FieldReadExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ExpressionToTaskConverter implements ExpressionVisitor<Scope<TaskSupplier>, Task> {
  private final Definitions definitions;
  private final TypeToSpecConverter toSpecConverter;
  private final NativeImplLoader nativeImplLoader;

  @Inject
  public ExpressionToTaskConverter(Definitions definitions, ObjectFactory objectFactory,
      NativeImplLoader nativeImplLoader) {
    this.toSpecConverter = new TypeToSpecConverter(objectFactory);
    this.definitions = definitions;
    this.nativeImplLoader = nativeImplLoader;
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, FieldReadExpression expression)
      throws ExpressionVisitorException {
    ItemSignature field = expression.field();
    StructType structType = (StructType) expression.expression().type();
    Algorithm algorithm = new ReadTupleElementAlgorithm(
        structType.fieldIndex(field.name().get()), field.type().visit(toSpecConverter));
    List<Task> children = childrenTasks(scope, expression.expression());
    return new NormalTask(CALL, field.type(), "." + field.name().get(), algorithm, children,
        expression.location(), true);
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, ReferenceExpression reference)
      throws ExpressionVisitorException {
    Value value = (Value) definitions.referencables().get(reference.name());
    if (value.body().isPresent()) {
      Task task = value.body().get().visit(scope, this);
      Task convertedTask = convertIfNeeded(task, value.type());
      return new VirtualTask(value.extendedName(), convertedTask, VALUE, reference.location());
    } else {
      Native nativ = loadNative(value);
      Algorithm algorithm = new CallNativeAlgorithm(value.type().visit(toSpecConverter), nativ);
      return new NormalTask(VALUE, value.type(), value.extendedName(), algorithm,
          ImmutableList.of(), reference.location(), nativ.cacheable());
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
  public Task visit(Scope<TaskSupplier> scope, ParameterReferenceExpression expression)
      throws ExpressionVisitorException {
    return scope.get(expression.name()).getTask();
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, CallExpression expression)
      throws ExpressionVisitorException {
    Callable callable = expression.callable();
    if (callable instanceof Function function) {
      var argumentTypes = map(expression.arguments(), e -> expressionType(scope, e));
      var variables = inferVariableBounds(function.type().parameterTypes(), argumentTypes, LOWER);
      Type actualResultType = function.resultType().mapVariables(variables, LOWER);

      if (function.body().isPresent()) {
        var arguments = childrenTaskSuppliers(scope, expression.arguments(), argumentTypes);
        return taskForDefinedFunction(
            scope, actualResultType, function, arguments, expression.location());
      } else {
        var arguments = childrenTasks(scope, expression.arguments());
        return taskForNativeFunction(arguments, function, variables, actualResultType,
            expression.location());
      }
    } else if (callable instanceof Constructor constructor) {
      return taskForConstructorCall(scope, constructor, expression);
    } else {
      throw new RuntimeException("Unexpected case: " + callable.getClass().getCanonicalName());
    }
  }

  private Task taskForConstructorCall(Scope<TaskSupplier> scope, Constructor constructor,
      CallExpression expression) throws ExpressionVisitorException {
    Type resultType = constructor.type().resultType();
    TupleSpec type = (TupleSpec) resultType.visit(toSpecConverter);
    Algorithm algorithm = new CreateTupleAlgorithm(type);
    List<Task> dependencies = childrenTasks(scope, expression.arguments());
    return new NormalTask(CALL, resultType, constructor.extendedName(), algorithm,
        dependencies, expression.location(), true);
  }

  private Task taskForDefinedFunction(Scope<TaskSupplier> scope, Type actualResultType,
      Function function, List<TaskSupplier> arguments, Location location)
      throws ExpressionVisitorException {
    var newScope = new Scope<>(scope, nameToArgumentMap(function.parameters(), arguments));
    Task callTask = function.body().get().visit(newScope, this);
    Task task = convertIfNeeded(callTask, actualResultType);
    return new VirtualTask(function.extendedName(), task, CALL, location);
  }

  private Task taskForNativeFunction(List<Task> arguments, Function function,
      BoundedVariables variables, Type actualResultType, Location location)
      throws ExpressionVisitorException {
    Native nativ = loadNative(function);
    Algorithm algorithm = new CallNativeAlgorithm(actualResultType.visit(toSpecConverter), nativ);
    ImmutableList<Type> actualParameterTypes =
        map(function.type().parameterTypes(), t -> t.mapVariables(variables, LOWER));
    List<Task> dependencies = convertedArguments(actualParameterTypes, arguments);
    if (function.name().equals(IF_FUNCTION_NAME)) {
      return new IfTask(actualResultType, algorithm, dependencies, location, nativ.cacheable());
    } else {
      return new NormalTask(CALL, actualResultType, function.extendedName(), algorithm,
          dependencies, location, nativ.cacheable());
    }
  }

  private static ImmutableMap<String, TaskSupplier> nameToArgumentMap(
      List<Item> names, List<TaskSupplier> arguments) {
    Builder<String, TaskSupplier> builder = ImmutableMap.builder();
    for (int i = 0; i < arguments.size(); i++) {
      builder.put(names.get(i).name(), arguments.get(i));
    }
    return builder.build();
  }

  private static Type expressionType(Scope<TaskSupplier> scope, Expression expression) {
    if (expression instanceof ParameterReferenceExpression parameterReference) {
      return scope.get(parameterReference.name()).type();
    } else {
      return expression.type();
    }
  }

  private Native loadNative(Function function) throws ExpressionVisitorException {
    try {
      return nativeImplLoader.loadNative(function);
    } catch (LoadingNativeImplException e) {
      throw new ExpressionVisitorException(e.getMessage(), e);
    }
  }

  private List<Task> convertedArguments(List<Type> actualParameterTypes, List<Task> arguments) {
    List<Task> result = new ArrayList<>();
    for (int i = 0; i < arguments.size(); i++) {
      Type type = actualParameterTypes.get(i);
      result.add(convertIfNeeded(arguments.get(i), type));
    }
    return result;
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, ArrayLiteralExpression expression)
      throws ExpressionVisitorException {
    List<Task> elements = childrenTasks(scope, expression.elements());
    ArrayType actualType = arrayType(elements, expression.type());

    Algorithm algorithm = new CreateArrayAlgorithm(toSpecConverter.visit(actualType));
    List<Task> convertedElements = convertedElements(actualType.elemType(), elements);
    return new NormalTask(LITERAL, actualType, actualType.name(), algorithm, convertedElements,
        expression.location(), true);
  }

  private List<Task> convertedElements(Type type, List<Task> elements) {
    return map(elements, t -> convertIfNeeded(t, type));
  }

  private ArrayType arrayType(List<Task> elements, ArrayType arrayType) {
    return elements
        .stream()
        .map(Task::type)
        .reduce((typeA, typeB) -> typeA.mergeWith(typeB, UPPER))
        .map(Types::array)
        .orElse(arrayType);
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, BlobLiteralExpression expression) {
    var blobSpec = toSpecConverter.visit(blob());
    var algorithm = new FixedBlobAlgorithm(blobSpec, expression.byteString());
    return new NormalTask(LITERAL, blob(), algorithm.shortedLiteral(), algorithm,
        ImmutableList.of(), expression.location(), true);
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, StringLiteralExpression expression) {
    var stringType = toSpecConverter.visit(string());
    var algorithm = new FixedStringAlgorithm(stringType, expression.string());
    return new NormalTask(LITERAL, string(), algorithm.shortedString(), algorithm,
        ImmutableList.of(), expression.location(), true);
  }

  private ImmutableList<TaskSupplier> childrenTaskSuppliers(Scope<TaskSupplier> scope,
      ImmutableList<Expression> expressions, ImmutableList<Type> argumentTypes) {
    return zip(expressions, argumentTypes,
        (e, t) -> new TaskSupplier(t, () -> e.visit(scope, this)));
  }

  private List<Task> childrenTasks(Scope<TaskSupplier> scope, List<Expression> children)
      throws ExpressionVisitorException {
    ImmutableList.Builder<Task> builder = ImmutableList.builder();
    for (Expression child : children) {
      builder.add(child.visit(scope, this));
    }
    return builder.build();
  }

  private ImmutableList<Task> childrenTasks(Scope<TaskSupplier> scope, Expression expression)
      throws ExpressionVisitorException {
    return ImmutableList.of(expression.visit(scope, this));
  }

  private Task convertIfNeeded(Task task, Type requiredType) {
    if (task.type().equals(requiredType)) {
      return task;
    } else {
      return convert(requiredType, task);
    }
  }

  private NormalTask convert(Type requiredType, Task task) {
    String description = requiredType.name() + "<-" + task.type().name();
    Algorithm algorithm = new ConvertAlgorithm(requiredType.visit(toSpecConverter));
    List<Task> dependencies = list(task);
    return new NormalTask(
        CONVERSION, requiredType, description, algorithm, dependencies, task.location(), true);
  }
}

package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.function.Accessor;
import org.smoothbuild.lang.function.Constructor;
import org.smoothbuild.lang.function.DefinedFunction;
import org.smoothbuild.lang.function.NativeFunction;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.Container;

import com.google.common.hash.HashCode;

public class Evaluator {
  private final Computation computation;
  private final String name;
  private final boolean isInternal;
  private final boolean isCacheable;
  private final Location location;

  public static Evaluator valueEvaluator(Value value, Location location) {
    return new Evaluator(new ValueComputation(value), value.type().name(), true, true, location);
  }

  public static Evaluator arrayEvaluator(ArrayType arrayType, Location location) {
    return new Evaluator(new ArrayComputation(arrayType), arrayType.name(), true, true, location);
  }

  public static Evaluator nativeCallEvaluator(NativeFunction function, Location location) {
    return new Evaluator(new NativeCallComputation(function), function.name().toString(),
        false, function.isCacheable(), location);
  }

  public static Evaluator callEvaluator(DefinedFunction function, Location location) {
    return new Evaluator(new IdentityComputation(function.type()), function.name().toString(),
        false, true, location);
  }

  public static Evaluator constructorCallEvaluator(Constructor constructor, Location location) {
    return new Evaluator(new ConstructorCallComputation(constructor), constructor.name(),
        false, true, location);
  }

  public static Evaluator accessorCallEvaluator(Accessor accessor, Location location) {
    return new Evaluator(new AccessorCallComputation(accessor), accessor.name(),
        false, true, location);
  }

  public static Evaluator convertEvaluator(Type type, Location location) {
    return new Evaluator(
        new ConvertComputation(type), "conversion_from_generic", true, true, location);
  }

  public Evaluator(Computation computation, String name, boolean isInternal, boolean isCacheable,
      Location location) {
    this.computation = computation;
    this.name = checkNotNull(name);
    this.isInternal = isInternal;
    this.isCacheable = isCacheable;
    this.location = checkNotNull(location);
  }

  public HashCode hash() {
    return computation.hash();
  }

  public Type resultType() {
    return computation.resultType();
  }

  public String name() {
    return name;
  }

  public boolean isInternal() {
    return isInternal;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  public Location location() {
    return location;
  }

  public Output evaluate(Input input, Container container) {
    return computation.execute(input, container);
  }
}

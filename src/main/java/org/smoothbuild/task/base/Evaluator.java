package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class Evaluator {
  private final Computation computation;
  private final String name;
  private final boolean isInternal;
  private final boolean isCacheable;
  private final Location location;
  private final ImmutableList<Evaluator> dependencies;

  public static Evaluator valueEvaluator(Value value, Location location) {
    return new Evaluator(new ValueComputation(value), value.type().name(), true, true, location,
        ImmutableList.of());
  }

  public static Evaluator arrayEvaluator(ArrayType arrayType, Location location,
      List<Evaluator> dependencies) {
    return new Evaluator(new ArrayComputation(arrayType), arrayType.name(), true, true,
        location,
        dependencies);
  }

  public static Evaluator nativeCallEvaluator(NativeFunction function, boolean isInternal,
      Location location, List<Evaluator> dependencies) {
    return new Evaluator(new NativeCallComputation(function), function.name().toString(),
        isInternal, function.isCacheable(), location, dependencies);
  }

  public static Evaluator virtualEvaluator(DefinedFunction function, Location location,
      List<Evaluator> dependencies) {
    return new Evaluator(new IdentityComputation(function.type()), function.name().toString(),
        false, true, location, dependencies);
  }

  public Evaluator(Computation computation, String name, boolean isInternal, boolean isCacheable,
      Location location, List<Evaluator> dependencies) {
    this.computation = computation;
    this.name = checkNotNull(name);
    this.isInternal = isInternal;
    this.isCacheable = isCacheable;
    this.location = checkNotNull(location);
    this.dependencies = ImmutableList.copyOf(dependencies);
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

  public ImmutableList<Evaluator> dependencies() {
    return dependencies;
  }

  public Output evaluate(Input input, ContainerImpl container) {
    return computation.execute(input, container);
  }
}

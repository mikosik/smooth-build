package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.task.exec.Container;

import com.google.common.collect.ImmutableList;

public class Evaluator {
  private final Computation computation;
  private final String name;
  private final boolean isInternal;
  private final boolean isCacheable;
  private final Location location;
  private final ImmutableList<Evaluator> children;

  public static Evaluator valueEvaluator(SObject object, Location location) {
    return new Evaluator(new ValueComputation(object), object.type().name(), true, true,
        ImmutableList.of(), location);
  }

  public static Evaluator arrayEvaluator(ConcreteArrayType arrayType,
      List<? extends Evaluator> children, Location location) {
    return new Evaluator(new ArrayComputation(arrayType), arrayType.name(), true, true, children,
        location);
  }

  public static Evaluator nativeCallEvaluator(ConcreteType type, NativeFunction function,
      List<? extends Evaluator> children, Location location) {
    return new Evaluator(new NativeCallComputation(type, function), function.name(),
        false, function.isCacheable(), children, location);
  }

  public static Evaluator identityEvaluator(ConcreteType type, String name, boolean isInternal,
      Evaluator evaluator, Location location) {
    return new Evaluator(new IdentityComputation(type), name, isInternal, true, ImmutableList.of(
        evaluator), location);
  }

  public static Evaluator constructorCallEvaluator(Constructor constructor,
      List<? extends Evaluator> children, Location location) {
    return new Evaluator(new ConstructorCallComputation(constructor), constructor.name(),
        false, true, children, location);
  }

  public static Evaluator accessorCallEvaluator(Accessor accessor,
      List<? extends Evaluator> children, Location location) {
    return new Evaluator(new AccessorCallComputation(accessor), accessor.name(),
        false, true, children, location);
  }

  public static Evaluator convertEvaluator(ConcreteType type, List<? extends Evaluator> children,
      Location location) {
    return new Evaluator(
        new ConvertComputation(type), "~conversion", true, true, children, location);
  }

  public Evaluator(Computation computation, String name, boolean isInternal, boolean isCacheable,
      List<? extends Evaluator> children, Location location) {
    this.computation = computation;
    this.name = checkNotNull(name);
    this.isInternal = isInternal;
    this.isCacheable = isCacheable;
    this.children = ImmutableList.copyOf(children);
    this.location = checkNotNull(location);
  }

  public Hash hash() {
    return computation.hash();
  }

  public ConcreteType type() {
    return computation.type();
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

  public ImmutableList<Evaluator> children() {
    return children;
  }

  public Location location() {
    return location;
  }

  public Output evaluate(Input input, Container container) throws ComputationException {
    return computation.execute(input, container);
  }

  public Evaluator convertIfNeeded(ConcreteType type) {
    if (type().equals(type)) {
      return this;
    } else {
      return convertEvaluator(type, list(this), location());
    }
  }
}

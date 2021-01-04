package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.util.Lists.allMatch;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.zip;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Type {
  private final String name;
  private final TypeConstructor typeConstructor;
  private final ImmutableList<Type> covariants;
  private final ImmutableList<Type> contravariants;
  protected final boolean isPolytype;

  protected Type(String name, TypeConstructor typeConstructor, boolean isPolytype) {
    this(name, typeConstructor, ImmutableList.of(), ImmutableList.of(), isPolytype);
  }

  protected Type(String name, TypeConstructor typeConstructor,
      ImmutableList<Type> covariants, ImmutableList<Type> contravariants, boolean isPolytype) {
    this.name = name;
    this.typeConstructor = typeConstructor;
    this.covariants = covariants;
    this.contravariants = contravariants;
    this.isPolytype = isPolytype;
  }

  public String name() {
    return name;
  }

  public String q() {
    return "`" + name + "`";
  }

  /**
   * @return true iff this type contains type variable(s).
   */
  public boolean isPolytype() {
    return isPolytype;
  }

  public boolean inequal(Type that, Side side) {
    return inequalByEdgeCases(that, side)
        || inequalByConstruction(that, side, s -> (Type a, Type b) -> a.inequal(b, s));
  }

  public boolean inequalParam(Type that, Side side) {
    return inequalByEdgeCases(that, side)
        || ((this instanceof Variable) && (side == LOWER || that instanceof Variable))
        || inequalByConstruction(that, side, s -> (a, b) -> a.inequalParam(b, s));
  }

  private boolean inequalByEdgeCases(Type that, Side side) {
    return that.equals(side.edge())
        || this.equals(side.reversed().edge());
  }

  private boolean inequalByConstruction(Type that, Side side,
      Function<Side, BiFunction<Type, Type, Boolean>> inequalityFunctionProducer) {
    var inequalFunction = inequalityFunctionProducer.apply(side);
    var inequalReversedFunction = inequalityFunctionProducer.apply(side.reversed());
    return this.typeConstructor.equals(that.typeConstructor)
        && allMatch(covariants, that.covariants, inequalFunction)
        && allMatch(contravariants, that.contravariants, inequalReversedFunction);
  }

  public boolean isAssignableFrom(Type type) {
    return inequal(type, LOWER);
  }

  public boolean isParamAssignableFrom(Type type) {
    return inequalParam(type, LOWER) && inferVariableBounds(type, LOWER).areConsistent();
  }

  public Type mapVariables(BoundedVariables boundedVariables, Side side) {
    if (isPolytype) {
      if (this instanceof Variable) {
        return boundedVariables.boundsMap().get(this).get(side);
      } else {
        return typeConstructor.construct(
            map(covariants, c -> c.mapVariables(boundedVariables, side)),
            map(contravariants, c -> c.mapVariables(boundedVariables, side.reversed())));
      }
    } else {
      return this;
    }
  }

  public static BoundedVariables inferVariableBounds(
      List<Type> typesA, List<Type> typesB, Side side) {
    return reduce(zip(typesA, typesB, inferFunction(side)));
  }

  public BoundedVariables inferVariableBounds(Type that, Side side) {
    if (that.equals(side.edge())) {
      return inferVariableBoundFromEdge(side);
    } else if (this.typeConstructor.equals(that.typeConstructor)) {
      return reduce(
          zip(covariants, that.covariants, inferFunction(side)),
          zip(contravariants, that.contravariants, inferFunction(side.reversed())));
    } else {
      return BoundedVariables.empty();
    }
  }

  private static BiFunction<Type, Type, BoundedVariables> inferFunction(Side side) {
    return (Type a, Type b) -> a.inferVariableBounds(b, side);
  }

  private BoundedVariables inferVariableBoundFromEdge(Side side) {
    Side reversed = side.reversed();
    return reduce(
        map(covariants, t -> t.inferVariableBounds(side.edge(), side)),
        map(contravariants, t -> t.inferVariableBounds(reversed.edge(), reversed)));
  }

  private BoundedVariables reduce(List<BoundedVariables> listA, List<BoundedVariables> listB) {
    return reduce(listA).mergeWith(reduce(listB));
  }

  private static BoundedVariables reduce(List<BoundedVariables> list) {
    return list.stream().reduce(BoundedVariables.empty(), BoundedVariables::mergeWith);
  }

  public Type mergeWith(Type that, Side direction) {
    Side reversed = direction.reversed();
    Type reversedEdge = reversed.edge();
    if (reversedEdge.equals(that)) {
      return this;
    } else if (reversedEdge.equals(this)) {
      return that;
    } else if (this.equals(that)) {
      return this;
    } else if (this.typeConstructor.equals(that.typeConstructor)) {
      var covar = zip(covariants, that.covariants, mergeWithFunction(direction));
      var contravar = zip(contravariants, that.contravariants, mergeWithFunction(reversed));
      return typeConstructor.construct(covar, contravar);
    } else {
      return direction.edge();
    }
  }

  private static BiFunction<Type, Type, Type> mergeWithFunction(Side direction) {
    return (a, b) -> a.mergeWith(b, direction);
  }

  public abstract <T> T visit(TypeVisitor<T> visitor);

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object instanceof Type that) {
      return this.name().equals(that.name());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }

  @Override
  public String toString() {
    return "Type(`" + name() + "`)";
  }
}

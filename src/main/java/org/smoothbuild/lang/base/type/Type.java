package org.smoothbuild.lang.base.type;

import static com.google.common.collect.Iterables.concat;
import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.BoundsMap.boundsMap;
import static org.smoothbuild.lang.base.type.BoundsMap.merge;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.util.Lists.allMatch;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.zip;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Type {
  private final String name;
  private final TypeConstructor typeConstructor;
  private final ImmutableList<Type> covariants;
  private final ImmutableList<Type> contravariants;
  private final ImmutableSet<Variable> variables;

  protected Type(String name, TypeConstructor typeConstructor, ImmutableSet<Variable> variables) {
    this(name, typeConstructor, list(), list(), variables);
  }

  protected Type(String name, TypeConstructor typeConstructor, ImmutableList<Type> covariants,
      ImmutableList<Type> contravariants, ImmutableSet<Variable> variables) {
    this.name = name;
    this.typeConstructor = typeConstructor;
    this.covariants = covariants;
    this.contravariants = contravariants;
    this.variables = variables;
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
    return !variables().isEmpty();
  }

  /**
   * @return type variables sorted alphabetically
   */
  public ImmutableSet<Variable> variables() {
      return variables;
  }

  public boolean isAssignableFrom(Type type) {
    return inequal(type, LOWER);
  }

  public boolean isParamAssignableFrom(Type type) {
    return inequalParam(type, LOWER) && inferVariableBounds(type, LOWER).areConsistent();
  }

  private boolean inequal(Type that, Side side) {
    return inequalImpl(that, side, (a, b) -> s -> a.inequal(b, s));
  }

  private boolean inequalParam(Type that, Side side) {
    return (this instanceof Variable)
        || inequalImpl(that, side, (a, b) -> s -> a.inequalParam(b, s));
  }

  private boolean inequalImpl(Type that, Side side,
      BiFunction<Type, Type, Function<Side, Boolean>> inequalityFunction) {
    return inequalByEdgeCases(that, side)
        || inequalByConstruction(that, side, inequalityFunction);
  }

  private boolean inequalByEdgeCases(Type that, Side side) {
    return that.equals(side.edge())
        || this.equals(side.reversed().edge());
  }

  private boolean inequalByConstruction(Type that, Side s,
      BiFunction<Type, Type, Function<Side, Boolean>> f) {
    return this.typeConstructor.equals(that.typeConstructor)
        && allMatch(covariants, that.covariants, (a, b) -> f.apply(a, b).apply(s))
        && allMatch(contravariants, that.contravariants, (a, b) -> f.apply(a, b).apply(s.reversed()));
  }

  public Type mapVariables(BoundsMap boundsMap, Side side) {
    if (isPolytype()) {
      if (this instanceof Variable) {
        return boundsMap.map().get(this).bounds().get(side);
      } else {
        return typeConstructor.construct(
            map(covariants, c -> c.mapVariables(boundsMap, side)),
            map(contravariants, c -> c.mapVariables(boundsMap, side.reversed())));
      }
    } else {
      return this;
    }
  }

  public static BoundsMap inferVariableBounds(
      List<Type> typesA, List<Type> typesB, Side side) {
    return BoundsMap.merge(zip(typesA, typesB, inferFunction(side)));
  }

  public BoundsMap inferVariableBounds(Type that, Side side) {
    if (this instanceof Variable variable) {
      return boundsMap(new Bounded(variable, oneSideBound(side, that)));
    } else if (that.equals(side.edge())) {
      return inferVariableBoundFromEdge(side);
    } else if (this.typeConstructor.equals(that.typeConstructor)) {
      return merge(concat(
          zip(covariants, that.covariants, inferFunction(side)),
          zip(contravariants, that.contravariants, inferFunction(side.reversed()))));
    } else {
      return boundsMap();
    }
  }

  private static BiFunction<Type, Type, BoundsMap> inferFunction(Side side) {
    return (Type a, Type b) -> a.inferVariableBounds(b, side);
  }

  private BoundsMap inferVariableBoundFromEdge(Side side) {
    Side reversed = side.reversed();
    return merge(concat(
        map(covariants, t -> t.inferVariableBounds(side.edge(), side)),
        map(contravariants, t1 -> t1.inferVariableBounds(reversed.edge(), reversed))));
  }

  public Type mergeWith(Type that, Side direction) {
    Side reversed = direction.reversed();
    Type reversedEdge = reversed.edge();
    if (reversedEdge.equals(that)) {
      return this;
    } else if (reversedEdge.equals(this)) {
      return that;
    } else if (this.equals(that)) {
      return this.strip();
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

  public Type strip() {
    if (this instanceof FunctionType || this instanceof ArrayType) {
      return typeConstructor.construct(stripTypes(covariants), stripTypes(contravariants));
    } else {
      return this;
    }
  }

  private ImmutableList<Type> stripTypes(ImmutableList<Type> types) {
    return map(types, Type::strip);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Type that
        && this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }

  @Override
  public String toString() {
    return "Type(`" + name() + "`)";
  }

  public static ImmutableList<ItemSignature> toItemSignatures(List<Type> types) {
    return map(types, t -> new ItemSignature(t, Optional.empty(), Optional.empty()));
  }
}

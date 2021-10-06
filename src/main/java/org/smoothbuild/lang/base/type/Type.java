package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.smoothbuild.lang.base.type.Sides.Side;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Type {
  private final String name;
  private final ImmutableSet<Variable> variables;

  protected Type(String name, ImmutableSet<Variable> variables) {
    this.name = name;
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

  public boolean contains(Type that) {
    return this.equals(that);
  }

  /**
   * @return type variables sorted alphabetically
   */
  public ImmutableSet<Variable> variables() {
      return variables;
  }

  Type mapVariables(BoundsMap boundsMap, Side side, TypeFactory typeFactory) {
    return this;
  }

  Type strip(TypeFactory typeFactory) {
    return this;
  }

  Type merge(Type that, Side direction, TypeFactory typeFactory) {
    Type reversedEdge = direction.reversed().edge();
    if (reversedEdge.equals(that)) {
      return this;
    } else if (reversedEdge.equals(this)) {
      return that;
    } else if (this.equals(that)) {
      return strip(typeFactory);
    } else {
      return mergeImpl(that, direction, typeFactory);
    }
  }

  protected Type mergeImpl(Type that, Side direction, TypeFactory typeFactory) {
    return direction.edge();
  }

  public void inferVariableBounds(Type source, Side side, TypeFactory typeFactory,
      Map<Variable, Bounded> result) {
  }

  public boolean inequal(Type that, Side side) {
    return inequalImpl(that, side, Type::inequal);
  }

  public boolean inequalParam(Type that, Side side) {
    return (this instanceof Variable)
        || inequalImpl(that, side, Type::inequalParam);
  }

  private boolean inequalImpl(Type that, Side side, InequalFunction inequalityFunction) {
    return inequalByEdgeCases(that, side)
        || inequalByConstruction(that, side, inequalityFunction);
  }

  private boolean inequalByEdgeCases(Type that, Side side) {
    return that.equals(side.edge())
        || this.equals(side.reversed().edge());
  }

  protected boolean inequalByConstruction(Type that, Side side, InequalFunction isInequal) {
    return this.name.equals(that.name);
  }

  public static interface InequalFunction {
    public boolean apply(Type typeA, Type typeB, Side side);
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
    return map(types, ItemSignature::itemSignature);
  }
}

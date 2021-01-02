package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.zip;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import org.smoothbuild.util.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Type {
  protected final boolean isPolytype;
  private final String name;

  protected Type(String name, boolean isPolytype) {
    this.name = name;
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

  public String typeConstructor() {
    return name();
  }

  public ImmutableList<Type> covariants() {
    return ImmutableList.of();
  }

  public ImmutableList<Type> contravariants() {
    return ImmutableList.of();
  }

  public boolean inequal(Type that, Side side) {
    return that.equals(side.edge())
        || this.equals(side.reversed().edge())
        || inequalByConstruction(that, side);
  }

  private boolean inequalByConstruction(Type that, Side side) {
    return this.typeConstructor().equals(that.typeConstructor())
        && allMatch(this.covariants(), that.covariants(), side)
        && allMatch(this.contravariants(), that.contravariants(), side.reversed());
  }

  private static boolean allMatch(List<Type> listA, List<Type> listB, Side side) {
    return Lists.allMatch(listA, listB, (Type a, Type b) -> a.inequal(b, side));
  }

  public boolean isAssignableFrom(Type type) {
    return inequal(type, LOWER);
  }

  public boolean isParamAssignableFrom(Type type) {
    return isAssignableFrom(type, true);
  }

  public Type mapVariables(VariableToBounds variableToBounds, Side side) {
    return this;
  }

  protected boolean isAssignableFrom(Type type, boolean variableRenaming) {
    return (type instanceof NothingType) || this.equals(type);
  }

  public static VariableToBounds inferVariableBounds(
      List<Type> typesA, List<Type> typesB, Side side) {
    return reduce(zip(typesA, typesB, inferFunction(side)));
  }

  public VariableToBounds inferVariableBounds(Type that, Side side) {
    if (that.equals(side.edge())) {
      return inferVariableBoundFromEdge(side);
    } else if (this.typeConstructor().equals(that.typeConstructor())) {
      return reduce(
          zip(this.covariants(), that.covariants(), inferFunction(side)),
          zip(this.contravariants(), that.contravariants(), inferFunction(side.reversed())));
    } else {
      return VariableToBounds.empty();
    }
  }

  private static BiFunction<Type, Type, VariableToBounds> inferFunction(Side side) {
    return (Type a, Type b) -> a.inferVariableBounds(b, side);
  }

  private VariableToBounds inferVariableBoundFromEdge(Side side) {
    Side reversed = side.reversed();
    return reduce(
        map(covariants(), t -> t.inferVariableBounds(side.edge(), side)),
        map(contravariants(), t -> t.inferVariableBounds(reversed.edge(), reversed)));
  }

  private VariableToBounds reduce(List<VariableToBounds> listA, List<VariableToBounds> listB) {
    return reduce(listA).mergeWith(reduce(listB));
  }

  private static VariableToBounds reduce(List<VariableToBounds> list) {
    return list.stream().reduce(VariableToBounds.empty(), VariableToBounds::mergeWith);
  }

  public Type mergeWith(Type that, Side direction) {
    if (direction.reversed().edge().equals(that) || this.equals(that)) {
      return this;
    } else {
      return direction.edge();
    }
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

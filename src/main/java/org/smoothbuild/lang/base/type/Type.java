package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Side.LOWER;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.parse.ast.Named;
import org.smoothbuild.util.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Type implements Named {
  protected final boolean isPolytype;
  private final String name;
  private final Location location;

  protected Type(String name, Location location, boolean isPolytype) {
    this.name = name;
    this.location = location;
    this.isPolytype = isPolytype;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Location location() {
    return location;
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

  public Type mapTypeVariables(VariableToBounds variableToBounds, Side side) {
    return this;
  }

  protected boolean isAssignableFrom(Type type, boolean variableRenaming) {
    return (type instanceof NothingType) || this.equals(type);
  }

  public static VariableToBounds inferVariableBounds(
      List<Type> typesA, List<Type> typesB, Side side) {
    VariableToBounds variableToBounds = VariableToBounds.empty();
    for (int i = 0; i < typesA.size(); i++) {
      VariableToBounds inferred = typesA.get(i).inferVariableBounds(typesB.get(i), side);
      variableToBounds = variableToBounds.mergeWith(inferred);
    }
    return variableToBounds;
  }

  public VariableToBounds inferVariableBounds(Type type, Side side) {
    return VariableToBounds.empty();
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

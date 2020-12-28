package org.smoothbuild.lang.base.type;

import java.util.List;
import java.util.Objects;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.parse.ast.Named;

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

  public boolean isAssignableFrom(Type type) {
    return isAssignableFrom(type, false);
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

package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Types.any;
import static org.smoothbuild.lang.base.type.Types.nothing;

import java.util.Map;
import java.util.Objects;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.parse.ast.Named;

import com.google.common.collect.ImmutableMap;

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

  public Type mapTypeVariables(Map<TypeVariable, Type> map) {
    return this;
  }

  public Map<TypeVariable, Type> inferTypeVariables(Type source) {
    return ImmutableMap.of();
  }

  public boolean isAssignableFrom(Type type) {
    return (type instanceof NothingType) || this.equals(type);
  }

  public boolean isParamAssignableFrom(Type type) {
    return isAssignableFrom(type);
  }

  public Type joinWith(Type that) {
    if (that instanceof NothingType) {
      return this;
    } else if (this.equals(that)){
      return this;
    } else {
      return any();
    }
  }

  public Type meetWith(Type that) {
    if (this.equals(that)){
      return this;
    } else if (that instanceof AnyType) {
      return this;
    } else {
      return nothing();
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

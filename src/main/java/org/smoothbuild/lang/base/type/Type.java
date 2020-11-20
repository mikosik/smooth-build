package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Types.nothing;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.parse.ast.Named;

import com.google.common.collect.ImmutableMap;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Type implements Named {
  protected final boolean hasGenericTypeParameters;
  private final String name;
  private final Location location;

  protected Type(String name, Location location, boolean hasGenericTypeParameters) {
    this.name = name;
    this.location = location;
    this.hasGenericTypeParameters = hasGenericTypeParameters;
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

  public boolean isArray() {
    return false;
  }

  public boolean isNothing() {
    return this == nothing();
  }

  public boolean hasGenericTypeParameters() {
    return hasGenericTypeParameters;
  }

  public Type mapTypeParameters(Map<GenericType, Type> map) {
    return this;
  }

  public Map<GenericType, Type> inferTypeParametersMap(Type source) {
    return ImmutableMap.of();
  }

  public boolean isAssignableFrom(Type type) {
    return type.isNothing() || this.equals(type);
  }

  public boolean isParamAssignableFrom(Type type) {
    return isAssignableFrom(type);
  }

  public Optional<Type> leastUpperBound(Type that) {
    if (that.isNothing()) {
      return Optional.of(this);
    } else if (this.equals(that)){
      return Optional.of(this);
    } else {
      return Optional.empty();
    }
  }

  public Optional<Type> greatestLowerBound(Type that) {
    if (this.equals(that)){
      return Optional.of(this);
    } else {
      return Optional.of(Types.nothing());
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

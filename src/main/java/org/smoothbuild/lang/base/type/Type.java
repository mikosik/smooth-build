package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Types.nothing;

import java.util.Map;
import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.parse.ast.Named;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Type implements Named {
  protected final boolean isGeneric;
  private final String name;
  private final Location location;

  protected Type(String name, Location location, boolean isGeneric) {
    this.name = name;
    this.location = location;
    this.isGeneric = isGeneric;
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

  public boolean isGeneric() {
    return isGeneric;
  }

  public boolean isArray() {
    return false;
  }

  public boolean isNothing() {
    return this == nothing();
  }

  public Type coreType() {
    return this;
  }

  public Type mapTypeParameters(Map<GenericBasicType, Type> map) {
    throw newNotGenericException();
  }

  public Map<GenericBasicType, Type> inferTypeParametersMap(Type source) {
    throw newNotGenericException();
  }

  private UnsupportedOperationException newNotGenericException() {
    return new UnsupportedOperationException(toString() + " is not generic");
  }

  public boolean isAssignableFrom(Type type) {
    return type.isNothing() || this.equals(type);
  }

  public boolean isParamAssignableFrom(Type type) {
    if (isGeneric()) {
      return true;
    } else {
      return isAssignableFrom(type);
    }
  }

  public Optional<Type> commonSuperType(Type that) {
    if (that.isNothing()) {
      return Optional.of(this);
    } else if (this.equals(that)){
      return Optional.of(this);
    } else {
      return Optional.empty();
    }
  }

  public abstract <T> T visit(TypeVisitor<T> visitor);

  @Override
  public String toString() {
    return "Type(`" + name() + "`)";
  }
}

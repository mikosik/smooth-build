package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Types.nothing;

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

  public Type replaceCoreType(Type type) {
    return type;
  }

  public int coreDepth() {
    return 0;
  }

  public Type changeCoreDepthBy(int delta) {
    if (delta < 0) {
      throw new IllegalArgumentException(
          "It's not possible to reduce core depth of non array type.");
    }
    return increaseCoreDepth(delta);
  }

  public Type increaseCoreDepth(int delta) {
    Type result = this;
    for (int i = 0; i < delta; i++) {
      result = Types.array(result);
    }
    return result;
  }

  public boolean isAssignableFrom(Type type) {
    return type.isNothing() || this.equals(type);
  }

  public boolean isParamAssignableFrom(Type type) {
    if (isGeneric()) {
      if (type.coreType().isNothing()) {
        return true;
      }
      return coreDepth() <= type.coreDepth();
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

  public Type actualCoreTypeWhenAssignedFrom(Type source) {
    return source;
  }

  public abstract <T> T visit(TypeVisitor<T> visitor);

  @Override
  public String toString() {
    return "Type(`" + name() + "`)";
  }
}

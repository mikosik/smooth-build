package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;

import java.util.Objects;

/**
 * This class is immutable.
 */
public abstract class BasicType extends Type {
  public BasicType(String name) {
    this(name, false);
  }

  public BasicType(String name, boolean isGeneric){
      super(name, internal(), isGeneric);
  }

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
}


package org.smoothbuild.lang.type;

import java.util.Objects;

import org.smoothbuild.lang.value.Value;

/**
 * Type in smooth language.
 */
public class Type {
  private final String name;
  private final Class<? extends Value> jType;

  protected Type(String name, Class<? extends Value> jType) {
    this.name = name;
    this.jType = jType;
  }

  public String name() {
    return name;
  }

  public Class<? extends Value> jType() {
    return jType;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (!Type.class.equals(object.getClass())) {
      return false;
    }
    Type that = (Type) object;
    return this.name.equals(that.name) && this.jType.equals(that.jType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, jType);
  }

  @Override
  public String toString() {
    return name;
  }
}

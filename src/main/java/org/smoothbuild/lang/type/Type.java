package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.Value;

/**
 * Type in smooth language.
 */
public abstract class Type {
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

  public final boolean equals(Object object) {
    return this == object;
  }

  public final int hashCode() {
    return name.hashCode();
  }

  public String toString() {
    return name;
  }
}

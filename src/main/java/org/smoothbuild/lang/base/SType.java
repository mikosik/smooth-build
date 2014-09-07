package org.smoothbuild.lang.base;

import com.google.inject.TypeLiteral;

/**
 * Smooth type. Type in smooth language.
 */
public class SType<T extends SValue> {
  private final String name;
  private final TypeLiteral<? extends SValue> jType;

  protected static <T extends SValue> SType<T> sType(String name, TypeLiteral<T> jType) {
    return new SType<>(name, jType);
  }

  protected SType(String name, TypeLiteral<T> jType) {
    this.name = name;
    this.jType = jType;
  }

  public String name() {
    return name;
  }

  public TypeLiteral<? extends SValue> jType() {
    return jType;
  }

  @Override
  public final boolean equals(Object object) {
    return this == object;
  }

  @Override
  public final int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return "'" + name + "'";
  }
}

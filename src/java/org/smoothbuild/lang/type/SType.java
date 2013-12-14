package org.smoothbuild.lang.type;

import com.google.inject.TypeLiteral;

/**
 * Smooth type. Type in smooth language.
 */
public class SType<T extends SValue> {
  private final String name;
  private final TypeLiteral<? extends SValue> javaType;

  protected static <T extends SValue> SType<T> sType(String name, TypeLiteral<T> javaType) {
    return new SType<T>(name, javaType);
  }

  protected SType(String name, TypeLiteral<T> javaType) {
    this.name = name;
    this.javaType = javaType;
  }

  public String name() {
    return name;
  }

  public TypeLiteral<? extends SValue> javaType() {
    return javaType;
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

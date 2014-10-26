package org.smoothbuild.lang.base;

import com.google.inject.TypeLiteral;

/**
 * Type in smooth language.
 */
public class Type<T extends Value> {
  private final String name;
  private final TypeLiteral<? extends Value> jType;

  protected static <T extends Value> Type<T> type(String name, Class<T> clazz) {
    return new Type<>(name, TypeLiteral.get(clazz));
  }

  protected Type(String name, TypeLiteral<T> jType) {
    this.name = name;
    this.jType = jType;
  }

  public String name() {
    return name;
  }

  public TypeLiteral<? extends Value> jType() {
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

package org.smoothbuild.lang.type;

import com.google.common.collect.ImmutableList;
import com.google.inject.TypeLiteral;

public class Type<T extends Value> {
  private final String name;
  private final TypeLiteral<? extends Value> javaType;
  private final ImmutableList<Type<?>> superTypes;

  protected Type(String name, TypeLiteral<T> javaType, Type<?>... superTypes) {
    this.name = name;
    this.javaType = javaType;
    this.superTypes = ImmutableList.copyOf(superTypes);
  }

  public String name() {
    return name;
  }

  public TypeLiteral<? extends Value> javaType() {
    return javaType;
  }

  public ImmutableList<Type<?>> superTypes() {
    return superTypes;
  }

  public boolean isAssignableFrom(Type<?> type) {
    if (this == type) {
      return true;
    }
    for (Type<?> superType : type.superTypes) {
      if (this.isAssignableFrom(superType)) {
        return true;
      }
    }
    return false;
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

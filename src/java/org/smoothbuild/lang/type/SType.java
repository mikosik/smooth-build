package org.smoothbuild.lang.type;

import com.google.common.collect.ImmutableList;
import com.google.inject.TypeLiteral;

/**
 * Smooth type. Type in smooth language.
 */
public class SType<T extends SValue> {
  private final String name;
  private final TypeLiteral<? extends SValue> javaType;
  private final ImmutableList<SType<?>> superTypes;

  protected SType(String name, TypeLiteral<T> javaType, SType<?>... superTypes) {
    this.name = name;
    this.javaType = javaType;
    this.superTypes = ImmutableList.copyOf(superTypes);
  }

  public String name() {
    return name;
  }

  public TypeLiteral<? extends SValue> javaType() {
    return javaType;
  }

  public ImmutableList<SType<?>> superTypes() {
    return superTypes;
  }

  public boolean isAssignableFrom(SType<?> type) {
    if (this == type) {
      return true;
    }

    return type.superTypes.contains(this);
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

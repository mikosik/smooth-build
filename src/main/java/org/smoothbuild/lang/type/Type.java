package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Value;

import com.google.inject.TypeLiteral;

/**
 * Type in smooth language.
 */
public abstract class Type {
  private final String name;
  private final TypeLiteral<? extends Value> jType;

  protected Type(String name, Class<? extends Value> clazz) {
    this(name, TypeLiteral.get(clazz));
  }

  protected Type(String name, TypeLiteral<? extends Value> jType) {
    this.name = name;
    this.jType = jType;
  }

  public String name() {
    return name;
  }

  public TypeLiteral<? extends Value> jType() {
    return jType;
  }

  public abstract Value defaultValue(ValuesDb valuesDb);

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

package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.Value;

public class GenericType extends AbstractType {
  public GenericType(String name) {
    super(null, name, Value.class);
  }

  protected GenericType(String name, Class<? extends Value> jType) {
    super(null, name, jType);
  }

  @Override
  public GenericType coreType() {
    return this;
  }

  @Override
  public boolean isGeneric() {
    return true;
  }

  @Override
  public boolean isAssignableFrom(Type type) {
    return type.isGeneric() && name().equals(type.name());
  }

  @Override
  public Type commonSuperType(Type that) {
    if (this.equals(that)) {
      return this;
    }
    return null;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof GenericType && equals((GenericType) object);
  }

  private boolean equals(GenericType that) {
    return this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }

  @Override
  public String toString() {
    return "Type(\"" + name() + "\")";
  }
}

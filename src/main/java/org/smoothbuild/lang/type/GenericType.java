package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.value.Value;

public class GenericType extends AbstractType {
  public GenericType(String name) {
    super(null, name, Value.class);
  }

  protected GenericType(String name, Class<? extends Value> jType) {
    super(null, name, jType);
  }

  public <T extends Type> T actualCoreTypeWhenAssignedFrom(T type) {
    return type;
  }

  @Override
  public GenericType coreType() {
    return this;
  }

  @Override
  public GenericType increaseCoreDepthBy(int delta) {
    checkArgument(0 <= delta, "delta must be non negative value");
    GenericType result = this;
    for (int i = 0; i < delta; i++) {
      result = new GenericArrayType(result);
    }
    return result;
  }

  @Override
  public GenericType decreaseCoreDepthBy(int delta) {
    if (delta != 0) {
      throw new IllegalArgumentException(
          "It's not possible to reduce core depth of non array type.");
    }
    return this;
  }

  @Override
  public boolean isGeneric() {
    return true;
  }

  @Override
  public boolean isAssignableFrom(Type type) {
    if (type.isGeneric()) {
      return equals(type);
    } else {
      return type.coreType().isNothing() && type.coreDepth() <= coreDepth();
    }
  }

  @Override
  public boolean isArgAssignableFrom(Type type) {
    if (type.coreType().isNothing()) {
      return true;
    }
    return coreDepth() <= type.coreDepth();
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

package org.smoothbuild.lang.object.type;

import org.smoothbuild.lang.object.base.SObject;

public class GenericType extends AbstractType {
  public GenericType(String name) {
    super(null, name, SObject.class);
  }

  protected GenericType(String name, Class<? extends SObject> jType) {
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
  public GenericType changeCoreDepthBy(int delta) {
    if (delta < 0) {
      throw new IllegalArgumentException(
          "It's not possible to reduce core depth of non array type.");
    }
    GenericType result = this;
    for (int i = 0; i < delta; i++) {
      result = new GenericArrayType(result);
    }
    return result;
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
  public boolean isParamAssignableFrom(Type type) {
    if (type.coreType().isNothing()) {
      return true;
    }
    return coreDepth() <= type.coreDepth();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof GenericType
        && this.name().equals(((GenericType) object).name());
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

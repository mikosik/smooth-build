package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.smoothbuild.lang.object.db.ObjectFactory;

public class GenericArrayType extends GenericType implements ArrayType {
  private final GenericType elemType;

  public GenericArrayType(GenericType elemType) {
    super("[" +  elemType.name() + "]");
    this.elemType = requireNonNull(elemType);
  }

  @Override
  public GenericType elemType() {
    return elemType;
  }

  @Override
  public boolean isArray() {
    return true;
  }

  @Override
  public GenericType coreType() {
    return elemType.coreType();
  }

  @Override
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }

  @Override
  public <T extends Type> T replaceCoreType(T coreType) {
    @SuppressWarnings("unchecked")
    T result = (T) coreType.changeCoreDepthBy(coreDepth());
    return result;
  }

  @Override
  public GenericType changeCoreDepthBy(int delta) {
    if (delta < 0) {
      return elemType.changeCoreDepthBy(delta + 1);
    } else {
      return super.changeCoreDepthBy(delta);
    }
  }

  @Override
  public <T extends Type> T actualCoreTypeWhenAssignedFrom(T type) {
    if (type.isArray()) {
      @SuppressWarnings("unchecked")
      T result = (T) elemType.actualCoreTypeWhenAssignedFrom(((ArrayType) type).elemType());
      return result;
    } else if (type.isNothing()) {
      return type;
    } else {
      throw new IllegalArgumentException("Cannot assign " + name() + " from " + type.name());
    }
  }

  @Override
  public org.smoothbuild.lang.object.type.Type toRecordType(ObjectFactory objectFactory) {
    return objectFactory.arrayType(elemType.toRecordType(objectFactory));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof GenericArrayType that) {
      return this.elemType.equals(that.elemType);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }
}

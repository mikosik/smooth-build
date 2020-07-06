package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.smoothbuild.lang.object.db.ObjectFactory;

public class ConcreteArrayType extends ConcreteType implements ArrayType {
  private final ConcreteType elemType;

  public ConcreteArrayType(ConcreteType elemType) {
    super("[" +  elemType.name() + "]", calculateSuperType(elemType));
    this.elemType = requireNonNull(elemType);
  }

  @Override
  public ConcreteType elemType() {
    return elemType;
  }

  private static ConcreteType calculateSuperType(ConcreteType elemType) {
    ConcreteType elemSuperType = elemType.superType();
    if (elemSuperType == null) {
      return null;
    } else {
      return new ConcreteArrayType(elemSuperType);
    }
  }

  @Override
  public boolean isArray() {
    return true;
  }

  @Override
  public ConcreteType coreType() {
    return elemType.coreType();
  }

  @Override
  public <T extends Type> T replaceCoreType(T coreType) {
    @SuppressWarnings("unchecked")
    T result = (T) coreType.changeCoreDepthBy(coreDepth());
    return result;
  }

  @Override
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }

  @Override
  public ConcreteType changeCoreDepthBy(int delta) {
    if (delta < 0) {
      return elemType.changeCoreDepthBy(delta + 1);
    } else {
      return super.changeCoreDepthBy(delta);
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
    if (o instanceof ConcreteArrayType that) {
      return this.elemType.equals(that.elemType);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }
}

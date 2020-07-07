package org.smoothbuild.lang.base.type.compound;

import java.util.Objects;

import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.GenericType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.object.db.ObjectFactory;

public class ArrayCompoundability implements Compoundability {
  @Override
  public boolean isArray() {
    return true;
  }

  @Override
  public Type coreType(Type type) {
    return elemType(type).coreType();
  }

  @Override
  public int coreDepth(Type type) {
    return 1 + elemType(type).coreDepth();
  }

  @Override
  public Type changeCoreDepthBy(Type type, int delta) {
    if (delta < 0) {
      return elemType(type).changeCoreDepthBy(delta + 1);
    } else {
      return CompoundabilityUtils.increaseCoreDepth(type, delta);
    }
  }

  @Override
  public <T extends Type> T actualCoreTypeWhenAssignedFrom(Type destination, T source) {
    if (source.isArray()) {
      GenericType genericElemType = (GenericType) elemType(destination);
      @SuppressWarnings("unchecked")
      T result = (T) genericElemType.actualCoreTypeWhenAssignedFrom(((ArrayType) source).elemType());
      return result;
    } else if (source.isNothing()) {
      return source;
    } else {
      throw new IllegalArgumentException("Cannot assign " + destination + " from " + source.name());
    }
  }

  @Override
  public org.smoothbuild.lang.object.type.Type toRecordType(
      Type type, ObjectFactory objectFactory) {
    return objectFactory.arrayType(elemType(type).toRecordType(objectFactory));
  }

  @Override
  public boolean areEqual(Type type, Object object) {
    if (type == object) {
      return true;
    }
    if (type instanceof ArrayType thisArray && object instanceof ArrayType thatArray) {
      return thisArray.elemType().equals(thatArray.elemType());
    }
    return false;
  }

  @Override
  public int hashCode(Type type) {
    return Objects.hash(type.name());
  }

  private Type elemType(Type t) {
    return ((ArrayType) t).elemType();
  }
}

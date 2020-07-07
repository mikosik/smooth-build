package org.smoothbuild.lang.base.type.compound;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.Objects;

import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.object.db.ObjectFactory;

public class StructCompoundability extends DefaultCompoundability {
  @Override
  public org.smoothbuild.lang.object.type.Type toRecordType(
      Type type, ObjectFactory objectFactory) {
    StructType structType = (StructType) type;
    Iterable<org.smoothbuild.lang.object.type.Field> rFields =
        structType.fields().values().stream()
        .map(f -> new org.smoothbuild.lang.object.type.Field(
            (org.smoothbuild.lang.object.type.ConcreteType) f.type().toRecordType(objectFactory),
            f.name(),
            null))
        .collect(toImmutableList());
    return objectFactory.structType(structType.name(), rFields);
  }

  @Override
  public boolean areEqual(Type type, Object object) {
    if (type == object) {
      return true;
    }
    if (type instanceof StructType thisStruct && object instanceof StructType thatStruct) {
      return thisStruct.name().equals(thatStruct.name())
          && thisStruct.fields().equals(thatStruct.fields());
    }
    return false;
  }

  @Override
  public int hashCode(Type type) {
    return Objects.hash(type.name());
  }
}

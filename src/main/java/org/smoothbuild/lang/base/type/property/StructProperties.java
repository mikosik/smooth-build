package org.smoothbuild.lang.base.type.property;

import java.util.Objects;

import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;

public class StructProperties extends DefaultProperties {
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

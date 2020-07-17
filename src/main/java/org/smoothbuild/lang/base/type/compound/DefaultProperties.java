package org.smoothbuild.lang.base.type.compound;

import org.smoothbuild.lang.base.type.Type;

public abstract class DefaultProperties implements TypeProperties {
  @Override
  public boolean isArray() {
    return false;
  }

  @Override
  public Type coreType(Type type) {
    return type;
  }

  @Override
  public int coreDepth(Type type) {
    return 0;
  }

  @Override
  public Type changeCoreDepthBy(Type type, int delta) {
    if (delta < 0) {
      throw new IllegalArgumentException(
          "It's not possible to reduce core depth of non array type.");
    }
    return PropertiesUtils.increaseCoreDepth(type, delta);
  }

  @Override
  public <T extends Type> T actualCoreTypeWhenAssignedFrom(Type destination, T source) {
    return source;
  }
}

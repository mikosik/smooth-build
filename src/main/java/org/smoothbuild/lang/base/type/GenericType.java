package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.compound.Compoundability;

public class GenericType extends Type {
  public GenericType(String name, Location location, Compoundability compoundability) {
    super(name, location, compoundability);
  }

  @Override
  public boolean isGeneric() {
    return true;
  }

  @Override
  public GenericType superType() {
    return null;
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

  public <T extends Type> T actualCoreTypeWhenAssignedFrom(T source) {
    return compoundability.actualCoreTypeWhenAssignedFrom(this, source);
  }
}

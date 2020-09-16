package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.property.TypeProperties;

/**
 * This class is immutable.
 */
public class GenericType extends Type {
  public GenericType(String name, Location location, TypeProperties properties) {
    super(name, location, properties);
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

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }

  public <T extends Type> T actualCoreTypeWhenAssignedFrom(T source) {
    return properties.actualCoreTypeWhenAssignedFrom(this, source);
  }
}

package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Location.internal;

import org.smoothbuild.lang.base.type.property.ArrayProperties;

/**
 * This class is immutable.
 */
public class GenericArrayType extends GenericType implements ArrayType {
  private final GenericType elemType;

  public GenericArrayType(GenericType elemType) {
    super("[" +  elemType.name() + "]", internal(), new ArrayProperties());
    this.elemType = requireNonNull(elemType);
  }

  @Override
  public GenericType elemType() {
    return elemType;
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}

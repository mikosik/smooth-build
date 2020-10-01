package org.smoothbuild.lang.base.type;

import java.util.Map;

/**
 * This class is immutable.
 */
public class GenericType extends BasicType {
  public GenericType(String name) {
    super(name, true);
  }

  @Override
  public Type mapTypeParameters(Map<GenericType, Type> map) {
    return map.get(this);
  }

  @Override
  public Map<GenericType, Type> inferTypeParametersMap(Type source) {
    return Map.of(this, source);
  }

  @Override
  public boolean isParamAssignableFrom(Type type) {
    return true;
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}

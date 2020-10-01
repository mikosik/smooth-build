package org.smoothbuild.lang.base.type;

import java.util.Map;

/**
 * This class is immutable.
 */
public class GenericBasicType extends BasicType {
  public GenericBasicType(String name) {
    super(name, true);
  }

  @Override
  public Type mapTypeParameters(Map<GenericBasicType, Type> map) {
    return map.get(this);
  }

  @Override
  public Map<GenericBasicType, Type> inferTypeParametersMap(Type source) {
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

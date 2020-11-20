package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;

import java.util.Map;

/**
 * This class is immutable.
 */
public class TypeVariable extends Type {
  public TypeVariable(String name) {
    super(name, internal(), true);
  }

  @Override
  public Type mapTypeVariables(Map<TypeVariable, Type> map) {
    return map.get(this);
  }

  @Override
  public Map<TypeVariable, Type> inferTypeVariables(Type source) {
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

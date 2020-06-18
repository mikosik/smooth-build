package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.Type;

public record BasicType(String name) implements ConcreteType {
  @Override
  public Type toDType(ObjectFactory objectFactory) {
    return objectFactory.getType(name);
  }

  @Override
  public String q() {
    return "'" + name + "'";
  }
}

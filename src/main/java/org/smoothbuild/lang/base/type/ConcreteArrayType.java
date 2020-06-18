package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.Type;

public record ConcreteArrayType(String name, ValidType elemType)
    implements ConcreteType, ArrayType {
  public ConcreteArrayType(ConcreteType elemType) {
    this("[" +  elemType.name() + "]", elemType);
  }

  @Override
  public Type toDType(ObjectFactory objectFactory) {
    return objectFactory.arrayType(elemType.toDType(objectFactory));
  }

  @Override
  public String q() {
    return "'" + name + "'";
  }
}

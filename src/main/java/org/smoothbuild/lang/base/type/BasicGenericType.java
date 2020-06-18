package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.object.type.TypeNames.isGenericTypeName;

import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.Type;

public record BasicGenericType(String name) implements GenericType {
  public BasicGenericType {
    checkArgument(isGenericTypeName(name), "Illegal generic type name '%s'", name);
  }

  @Override
  public Type toDType(ObjectFactory objectFactory) {
    return objectFactory.getType(name);
  }

  @Override
  public String q() {
    return "'" + name + "'";
  }
}

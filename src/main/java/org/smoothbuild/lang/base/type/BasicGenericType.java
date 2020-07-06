package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.type.Types.isGenericTypeName;

import java.util.Objects;

import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.Type;

public class BasicGenericType extends GenericType {
  public BasicGenericType(String name) {
    super(name);
    checkArgument(isGenericTypeName(name), "Illegal generic type name '%s'", name);
  }

  @Override
  public Type toRecordType(ObjectFactory objectFactory) {
    return objectFactory.getType(name());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof BasicGenericType that) {
      return this.name().equals(that.name());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }
}

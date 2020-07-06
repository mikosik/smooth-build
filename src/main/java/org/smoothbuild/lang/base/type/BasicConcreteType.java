package org.smoothbuild.lang.base.type;

import java.util.Objects;

import org.smoothbuild.lang.object.db.ObjectFactory;

public class BasicConcreteType extends ConcreteType {
  public BasicConcreteType(String name) {
    super(name, null);
  }

  @Override
  public  org.smoothbuild.lang.object.type.Type toRecordType(ObjectFactory objectFactory) {
    return objectFactory.getType(name());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof BasicConcreteType that) {
      return this.name().equals(that.name());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }
}

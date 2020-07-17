package org.smoothbuild.lang.base.type.property;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.object.base.SObject;

public class BasicProperties extends DefaultProperties {
  private final Class<? extends SObject> jType;

  public BasicProperties(Class<? extends SObject> jType) {
    this.jType = jType;
  }

  @Override
  public boolean areEqual(Type type, Object object) {
    if (type == object) {
      return true;
    }
    if (object instanceof Type that) {
      return type.name().equals(that.name());
    }
    return false;
  }

  @Override
  public int hashCode(Type type) {
    return Objects.hash(type.name());
  }

  @Override
  public Class<? extends SObject> jType() {
    return jType;
  }
}

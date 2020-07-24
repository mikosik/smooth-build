package org.smoothbuild.lang.base.type.property;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;

public class BasicProperties extends DefaultProperties {
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
}

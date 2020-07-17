package org.smoothbuild.lang.base.type.property;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.record.base.Record;

public class BasicProperties extends DefaultProperties {
  private final Class<? extends Record> jType;

  public BasicProperties(Class<? extends Record> jType) {
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
  public Class<? extends Record> jType() {
    return jType;
  }
}

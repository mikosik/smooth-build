package org.smoothbuild.lang.base.type.compound;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.object.db.ObjectFactory;

public class BasicCompoundability extends DefaultCompoundability {
  @Override
  public  org.smoothbuild.lang.object.type.Type toRecordType(
      Type type, ObjectFactory objectFactory) {
    return objectFactory.getType(type.name());
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
}

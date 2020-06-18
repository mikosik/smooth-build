package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.object.db.ObjectFactory;

public interface ValidType extends Type {
  public org.smoothbuild.lang.object.type.Type toDType(ObjectFactory objectFactory);
}

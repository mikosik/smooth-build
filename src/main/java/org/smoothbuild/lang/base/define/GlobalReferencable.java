package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.api.Type;

public abstract class GlobalReferencable extends Referencable implements Evaluable {
  public GlobalReferencable(Type type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }
}

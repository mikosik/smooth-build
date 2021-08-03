package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.Type;

/**
 * This class is immutable.
 */
public abstract class Value extends GlobalReferencable {
  public Value(Type type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }
}



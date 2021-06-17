package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Location.internal;

import org.smoothbuild.lang.base.type.BaseType;

public class BaseTypeDefinition extends Defined {
  public BaseTypeDefinition(BaseType type) {
    super(type, type.name(), internal());
  }
}

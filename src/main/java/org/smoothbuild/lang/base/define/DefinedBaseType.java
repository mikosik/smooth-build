package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Location.internal;

import org.smoothbuild.lang.base.type.impl.BaseSType;

public class DefinedBaseType extends DefinedType {
  public DefinedBaseType(ModulePath modulePath, BaseSType type) {
    super(type, modulePath, type.name(), internal());
  }
}

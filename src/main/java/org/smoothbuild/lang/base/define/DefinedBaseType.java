package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Location.internal;

import org.smoothbuild.lang.base.type.BaseType;

public class DefinedBaseType extends DefinedType {
  public DefinedBaseType(ModulePath modulePath, BaseType type) {
    super(type, modulePath, type.name(), internal());
  }
}

package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Loc.internal;

import org.smoothbuild.lang.base.type.impl.BaseTypeS;

public class DefinedBaseType extends DefinedType {
  public DefinedBaseType(ModulePath modulePath, BaseTypeS type) {
    super(type, modulePath, type.name(), internal());
  }
}

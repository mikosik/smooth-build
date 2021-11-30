package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Loc.internal;

import org.smoothbuild.lang.base.type.impl.BaseTypeS;

public class DefBaseTypeS extends DefTypeS {
  public DefBaseTypeS(ModPath modPath, BaseTypeS type) {
    super(type, modPath, type.name(), internal());
  }
}

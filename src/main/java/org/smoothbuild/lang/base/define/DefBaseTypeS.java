package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Loc.internal;

import org.smoothbuild.lang.base.type.impl.BaseTS;

public class DefBaseTypeS extends DefTypeS {
  public DefBaseTypeS(ModPath modPath, BaseTS type) {
    super(type, modPath, type.name(), internal());
  }
}

package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.StructTS;

public class StructS extends DefTypeS {
  public StructS(StructTS type, ModPath modPath, Loc loc) {
    super(type, modPath, type.name(), loc);
  }
}

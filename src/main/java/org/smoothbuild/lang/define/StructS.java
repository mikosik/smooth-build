package org.smoothbuild.lang.define;

import org.smoothbuild.lang.type.impl.StructTS;

public class StructS extends DefTypeS {
  public StructS(StructTS type, ModPath modPath, Loc loc) {
    super(type, modPath, type.name(), loc);
  }
}

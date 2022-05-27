package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.StructTS;

public class StructDefS extends TDefS {
  public StructDefS(StructTS type, ModPath modPath, Loc loc) {
    super(type, modPath, type.name(), loc);
  }
}

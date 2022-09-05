package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.StructTS;

public class StructDefS extends TDefS {
  public StructDefS(StructTS type, ModPath modPath, Loc loc) {
    super(type, modPath, type.name(), loc);
  }
}

package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.StructTS;

public class StructDefS extends TDefS {
  public StructDefS(StructTS type, Loc loc) {
    super(type, type.name(), loc);
  }
}

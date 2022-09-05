package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.BaseTS;

public class BaseTDefS extends TDefS {
  public BaseTDefS(ModPath modPath, BaseTS type) {
    super(type, modPath, type.name(), Loc.internal());
  }
}

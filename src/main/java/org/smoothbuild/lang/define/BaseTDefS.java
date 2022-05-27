package org.smoothbuild.lang.define;

import org.smoothbuild.lang.type.BaseTS;

public class BaseTDefS extends TDefS {
  public BaseTDefS(ModPath modPath, BaseTS type) {
    super(type, modPath, type.name(), Loc.internal());
  }
}

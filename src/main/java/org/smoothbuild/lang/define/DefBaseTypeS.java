package org.smoothbuild.lang.define;

import org.smoothbuild.lang.type.BaseTS;

public class DefBaseTypeS extends DefTypeS {
  public DefBaseTypeS(ModPath modPath, BaseTS type) {
    super(type, modPath, type.name(), Loc.internal());
  }
}

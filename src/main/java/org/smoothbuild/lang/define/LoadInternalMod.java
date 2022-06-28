package org.smoothbuild.lang.define;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.type.BaseTS;
import org.smoothbuild.lang.type.TypeFS;

public class LoadInternalMod {
  public static ModS loadInternalMod() {
    var modPath = new ModPath("internal-module");
    var types = nList(map(TypeFS.baseTs(), t -> baseTDef(modPath, t)));
    return new ModS(modPath, null, types, nList());
  }

  private static TDefS baseTDef(ModPath modPath, BaseTS baseTS) {
    return new BaseTDefS(modPath, baseTS);
  }
}

package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
import static org.smoothbuild.util.collect.Maps.toMap;

import org.smoothbuild.compile.lang.type.BaseTS;
import org.smoothbuild.compile.lang.type.TypeFS;
import org.smoothbuild.compile.lang.type.TypeS;

public class LoadInternalMod {
  public static ModS loadInternalMod() {
    var modPath = new ModPath("internal-module");
    var types = immutableBindings(toMap(TypeFS.baseTs(), TypeS::name, t -> baseTDef(modPath, t)));
    return new ModS(modPath, null, types, immutableBindings());
  }

  private static TDefS baseTDef(ModPath modPath, BaseTS baseTS) {
    return new BaseTDefS(modPath, baseTS);
  }
}

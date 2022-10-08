package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
import static org.smoothbuild.util.collect.Maps.toMap;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeFS;
import org.smoothbuild.compile.lang.type.TypeS;

public class LoadInternalMod {
  public static ModuleS loadInternalModule() {
    var modPath = new ModPath("internal-module");
    var types = immutableBindings(toMap(TypeFS.baseTs(), TypeS::name, t -> baseTDef(modPath, t)));
    return new ModuleS(modPath, null, types, immutableBindings());
  }

  private static TDefS baseTDef(ModPath modPath, TypeS baseTS) {
    return new TDefS(baseTS, modPath, baseTS.name(), Loc.internal());
  }
}

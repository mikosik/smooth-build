package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
import static org.smoothbuild.util.collect.Maps.toMap;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeFS;
import org.smoothbuild.compile.lang.type.TypeS;

public class LoadInternalMod {
  public static ModuleS loadInternalModule() {
    var types = immutableBindings(toMap(TypeFS.baseTs(), TypeS::name, t -> baseTDef(t)));
    return new ModuleS(null, types, immutableBindings());
  }

  private static TDefS baseTDef(TypeS baseTS) {
    return new TDefS(baseTS, baseTS.name(), Loc.internal());
  }
}

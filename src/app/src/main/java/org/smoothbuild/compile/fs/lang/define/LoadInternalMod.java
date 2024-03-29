package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.compile.fs.lang.base.location.Locations.internalLocation;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.collect.Maps.toMap;

import org.smoothbuild.compile.fs.lang.type.TypeFS;
import org.smoothbuild.compile.fs.lang.type.TypeS;

public class LoadInternalMod {
  public static ModuleS loadInternalModule() {
    var types = immutableBindings(toMap(TypeFS.baseTs(), TypeS::name, t -> baseTypeDefinitions(t)));
    var members = new ScopeS(types, immutableBindings());
    return new ModuleS(members, members);
  }

  private static TypeDefinitionS baseTypeDefinitions(TypeS baseTypeS) {
    return new TypeDefinitionS(baseTypeS, internalLocation());
  }
}

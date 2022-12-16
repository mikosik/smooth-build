package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.compile.lang.base.location.Locations.internalLocation;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
import static org.smoothbuild.util.collect.Maps.toMap;

import org.smoothbuild.compile.lang.type.TypeFS;
import org.smoothbuild.compile.lang.type.TypeS;

public class LoadInternalMod {
  public static ModuleS loadInternalModule() {
    var types = immutableBindings(toMap(TypeFS.baseTs(), TypeS::name, t -> baseTypeDefinitions(t)));
    return new ModuleS(null, types, immutableBindings());
  }

  private static TypeDefinitionS baseTypeDefinitions(TypeS baseTypeS) {
    return new TypeDefinitionS(baseTypeS, internalLocation());
  }
}

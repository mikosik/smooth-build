package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;

import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.compilerfrontend.lang.base.location.Locations;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.lang.define.TypeDefinitionS;
import org.smoothbuild.compilerfrontend.lang.type.TypeFS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;

public class LoadInternalModuleMembers implements TryFunction<Tuple0, ScopeS> {
  @Override
  public Try<ScopeS> apply(Tuple0 unused) {
    var logger = new Logger();
    var types = immutableBindings(TypeFS.baseTs().toMap(TypeS::name, t -> baseTypeDefinitions(t)));
    return Try.of(new ScopeS(types, immutableBindings()), logger);
  }

  private static TypeDefinitionS baseTypeDefinitions(TypeS baseTypeS) {
    return new TypeDefinitionS(baseTypeS, Locations.internalLocation());
  }
}

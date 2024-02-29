package org.smoothbuild.compile.frontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.compile.frontend.lang.base.location.Locations.internalLocation;

import org.smoothbuild.common.log.Logger;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.compile.frontend.lang.define.TypeDefinitionS;
import org.smoothbuild.compile.frontend.lang.type.TypeFS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;

public class LoadInternalModuleMembers implements TryFunction<Tuple0, ScopeS> {
  @Override
  public Try<ScopeS> apply(Tuple0 unused) {
    var logger = new Logger();
    var types = immutableBindings(TypeFS.baseTs().toMap(TypeS::name, t -> baseTypeDefinitions(t)));
    return Try.of(new ScopeS(types, immutableBindings()), logger);
  }

  private static TypeDefinitionS baseTypeDefinitions(TypeS baseTypeS) {
    return new TypeDefinitionS(baseTypeS, internalLocation());
  }
}

package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;

import org.smoothbuild.common.dag.TryFunction0;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.lang.base.location.Locations;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.lang.define.TypeDefinitionS;
import org.smoothbuild.compilerfrontend.lang.type.TypeFS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;

public class LoadInternalModuleMembers implements TryFunction0<ScopeS> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "loadInternalModule");
  }

  @Override
  public Try<ScopeS> apply() {
    var logger = new Logger();
    var types = immutableBindings(TypeFS.baseTs().toMap(TypeS::name, t -> baseTypeDefinitions(t)));
    return Try.of(new ScopeS(types, immutableBindings()), logger);
  }

  private static TypeDefinitionS baseTypeDefinitions(TypeS baseTypeS) {
    return new TypeDefinitionS(baseTypeS, Locations.internalLocation());
  }
}

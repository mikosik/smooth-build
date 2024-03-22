package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;

import org.smoothbuild.common.dag.TryFunction0;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.lang.base.location.Locations;
import org.smoothbuild.compilerfrontend.lang.define.ModuleS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.lang.define.TypeDefinitionS;
import org.smoothbuild.compilerfrontend.lang.type.TypeFS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;

public class LoadInternalModuleMembers implements TryFunction0<ModuleS> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "loadInternalModule");
  }

  @Override
  public Try<ModuleS> apply() {
    var logger = new Logger();
    var types = immutableBindings(TypeFS.baseTs().toMap(TypeS::name, t -> baseTypeDefinitions(t)));
    var members = new ScopeS(types, immutableBindings());
    return Try.of(new ModuleS(members, members), logger);
  }

  private static TypeDefinitionS baseTypeDefinitions(TypeS baseTypeS) {
    return new TypeDefinitionS(baseTypeS, Locations.internalLocation());
  }
}

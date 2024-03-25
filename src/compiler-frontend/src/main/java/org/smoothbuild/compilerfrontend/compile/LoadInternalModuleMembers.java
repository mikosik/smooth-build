package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;

import org.smoothbuild.common.dag.TryFunction0;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.lang.base.location.Locations;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.define.STypeDefinition;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;

public class LoadInternalModuleMembers implements TryFunction0<SModule> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "loadInternalModule");
  }

  @Override
  public Try<SModule> apply() {
    var logger = new Logger();
    var types =
        immutableBindings(STypes.baseTypes().toMap(SType::name, t -> baseTypeDefinitions(t)));
    var members = new SScope(types, immutableBindings());
    return Try.of(new SModule(members, members), logger);
  }

  private static STypeDefinition baseTypeDefinitions(SType sBaseType) {
    return new STypeDefinition(sBaseType, Locations.internalLocation());
  }
}

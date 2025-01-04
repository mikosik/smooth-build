package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.lang.bindings.Bindings.immutableBindings;

import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.location.Locations;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.bindings.ImmutableFlatBindings;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.define.STypeDefinition;
import org.smoothbuild.compilerfrontend.lang.type.SBaseType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;

public class LoadInternalModuleMembers implements Task0<SModule> {
  @Override
  public Output<SModule> execute() {
    var label = COMPILER_FRONT_LABEL.append(":loadInternalModule");
    var logger = new Logger();
    ImmutableFlatBindings<STypeDefinition> types = immutableBindings(
        STypes.baseTypes().map(this::toTypeDefinition).toMap(Identifiable::name, t -> t));
    var members = new SScope(types, immutableBindings());
    var sModule = new SModule(members, members);
    return output(sModule, report(label, logger.toList()));
  }

  private STypeDefinition toTypeDefinition(SBaseType sBaseType) {
    return new STypeDefinition(sBaseType, sBaseType.id(), Locations.internalLocation());
  }
}

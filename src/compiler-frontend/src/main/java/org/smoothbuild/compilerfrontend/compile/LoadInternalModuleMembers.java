package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_FRONT_LABEL;

import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.compilerfrontend.lang.base.location.Locations;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.define.STypeDefinition;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;

public class LoadInternalModuleMembers implements Task0<SModule> {
  @Override
  public Output<SModule> execute() {
    var label = COMPILE_FRONT_LABEL.append("loadInternalModule");
    var logger = new Logger();
    var types =
        immutableBindings(STypes.baseTypes().toMap(SType::name, t -> baseTypeDefinitions(t)));
    var members = new SScope(types, immutableBindings());
    var sModule = new SModule(members, members);
    return output(sModule, report(label, new Trace(), EXECUTION, logger.toList()));
  }

  private static STypeDefinition baseTypeDefinitions(SType sBaseType) {
    return new STypeDefinition(sBaseType, Locations.internalLocation());
  }
}

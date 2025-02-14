package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.lang.name.Bindings.bindings;

import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.location.Locations;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.define.STypeDefinition;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;
import org.smoothbuild.compilerfrontend.lang.type.SBaseType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;

public class LoadInternalModuleMembers implements Task0<SModule> {
  @Override
  public Output<SModule> execute() {
    var label = COMPILER_FRONT_LABEL.append(":loadInternalModule");
    var logger = new Logger();
    var types = bindings(STypes.baseTypes().map(this::toTypeDefinition));
    Bindings<SNamedEvaluable> evaluables = bindings();
    var members = new SScope(types, evaluables);
    var sModule = new SModule(types, evaluables, members);
    return output(sModule, report(label, logger.toList()));
  }

  private STypeDefinition toTypeDefinition(SBaseType sBaseType) {
    return new STypeDefinition(sBaseType, sBaseType.fqn(), Locations.internalLocation());
  }
}

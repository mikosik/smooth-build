package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.lang.name.Bindings.bindings;
import static org.smoothbuild.compilerfrontend.lang.type.STypes.baseTypes;

import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.location.Locations;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.compilerfrontend.lang.base.HasName;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.define.STypeDefinition;
import org.smoothbuild.compilerfrontend.lang.type.SBaseType;

public class LoadInternalModuleMembers implements Task0<SModule> {
  @Override
  public Output<SModule> execute() {
    var label = COMPILER_FRONT_LABEL.append(":loadInternalModule");
    var logger = new Logger();
    var typeMap = baseTypes().toMap(HasName::name, this::toTypeDefinition);
    var members = new SScope(bindings(typeMap), bindings());
    var sModule = new SModule(typeMap, map(), members);
    return output(sModule, report(label, logger.toList()));
  }

  private STypeDefinition toTypeDefinition(SBaseType sBaseType) {
    return new STypeDefinition(sBaseType, sBaseType.fqn(), Locations.internalLocation());
  }
}

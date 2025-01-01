package org.smoothbuild.compilerfrontend;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.schedulingOutput;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.task.CompileModule;
import org.smoothbuild.compilerfrontend.compile.task.LoadInternalModuleMembers;
import org.smoothbuild.compilerfrontend.lang.define.SModule;

public class FrontendCompile implements Task1<List<FullPath>, SModule> {
  private final Scheduler scheduler;

  @Inject
  public FrontendCompile(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Output<SModule> execute(List<FullPath> modules) {
    var module = scheduler.submit(LoadInternalModuleMembers.class);
    for (var fullPath : modules) {
      module = scheduler.submit(CompileModule.class, module, argument(fullPath));
    }
    var report = report(COMPILER_FRONT_LABEL.append(":schedule"), list());
    return schedulingOutput(module, report);
  }
}

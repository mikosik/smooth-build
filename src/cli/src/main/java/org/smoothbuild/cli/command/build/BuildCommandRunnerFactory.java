package org.smoothbuild.cli.command.build;

import dagger.BindsInstance;
import dagger.Component;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import org.smoothbuild.cli.command.base.BaseModule;
import org.smoothbuild.cli.command.base.TaskRunner;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.TaskFilter;
import org.smoothbuild.common.log.report.TraceFilter;
import org.smoothbuild.virtualmachine.dagger.VmModule;

@Component(modules = {BaseModule.class, VmModule.class})
@PerCommand
public interface BuildCommandRunnerFactory {
  TaskRunner<ScheduleBuild> buildCommandRunner();

  @Component.Builder
  interface Builder {
    @BindsInstance
    Builder aliasPathMap(Map<Alias, Path> aliasPathMap);

    @BindsInstance
    Builder out(PrintWriter out);

    @BindsInstance
    Builder logLevel(Level logLevel);

    @BindsInstance
    Builder filterTasks(@TaskFilter Predicate<Report> filterTasks);

    @BindsInstance
    Builder filterTraces(@TraceFilter Predicate<Report> filterTraces);

    @BindsInstance
    Builder arguments(List<String> arguments);

    BuildCommandRunnerFactory build();
  }
}

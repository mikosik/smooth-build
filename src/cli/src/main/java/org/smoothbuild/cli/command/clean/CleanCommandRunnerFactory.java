package org.smoothbuild.cli.command.clean;

import dagger.BindsInstance;
import dagger.Component;
import java.io.PrintWriter;
import java.nio.file.Path;
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

@Component(modules = {BaseModule.class})
@PerCommand
public interface CleanCommandRunnerFactory {
  TaskRunner<ScheduleClean> cleanRunner();

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

    CleanCommandRunnerFactory build();
  }
}

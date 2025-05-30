package org.smoothbuild.cli.command.base;

import dagger.Module;
import org.smoothbuild.cli.dagger.CliModule;
import org.smoothbuild.common.filesystem.FileSystemModule;
import org.smoothbuild.common.init.InitializerModule;
import org.smoothbuild.common.log.report.ReportModule;
import org.smoothbuild.common.schedule.SchedulerModule;

@Module(
    includes = {
      CliModule.class,
      FileSystemModule.class,
      SchedulerModule.class,
      ReportModule.class,
      InitializerModule.class
    })
public interface BaseModule {}

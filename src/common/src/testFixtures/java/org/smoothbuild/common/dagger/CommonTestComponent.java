package org.smoothbuild.common.dagger;

import dagger.Component;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.init.InitializerModule;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.SchedulerModule;
import org.smoothbuild.common.testing.TestReporter;

@Component(modules = {ReportTestModule.class, SchedulerModule.class, InitializerModule.class})
@PerCommand
public interface CommonTestComponent {
  Scheduler scheduler();

  TestReporter reporter();

  Initializer initializer();
}

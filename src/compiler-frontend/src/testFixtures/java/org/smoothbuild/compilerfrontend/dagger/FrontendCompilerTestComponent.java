package org.smoothbuild.compilerfrontend.dagger;

import dagger.Component;
import org.smoothbuild.common.dagger.FileSystemTestModule;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.dagger.ReportTestModule;
import org.smoothbuild.common.init.InitializerModule;
import org.smoothbuild.common.schedule.SchedulerModule;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.virtualmachine.dagger.VmTestComponent;
import org.smoothbuild.virtualmachine.dagger.VmTestModule;

@Component(
    modules = {
      VmTestModule.class,
      FileSystemTestModule.class,
      ReportTestModule.class,
      SchedulerModule.class,
      InitializerModule.class,
    })
@PerCommand
public interface FrontendCompilerTestComponent extends VmTestComponent {
  FrontendCompile frontendCompile();
}

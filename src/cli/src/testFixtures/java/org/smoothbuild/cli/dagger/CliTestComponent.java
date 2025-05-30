package org.smoothbuild.cli.dagger;

import dagger.Component;
import org.smoothbuild.cli.command.build.SaveArtifacts;
import org.smoothbuild.common.dagger.FileSystemTestModule;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.dagger.ReportTestModule;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.init.InitializerModule;
import org.smoothbuild.common.schedule.SchedulerModule;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestComponent;
import org.smoothbuild.virtualmachine.dagger.VmTestModule;

@Component(
    modules = {
      CliTestModule.class,
      VmTestModule.class,
      FileSystemTestModule.class,
      ReportTestModule.class,
      SchedulerModule.class,
      InitializerModule.class,
    })
@PerCommand
public interface CliTestComponent extends FrontendCompilerTestComponent {
  SaveArtifacts saveArtifacts();

  @Artifacts
  FullPath artifactsPath();
}

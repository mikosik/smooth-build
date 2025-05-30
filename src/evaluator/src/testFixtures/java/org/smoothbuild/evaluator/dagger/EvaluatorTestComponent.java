package org.smoothbuild.evaluator.dagger;

import dagger.BindsInstance;
import dagger.Component;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.dagger.ReportTestModule;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.init.InitializerModule;
import org.smoothbuild.common.schedule.RunnableScheduler;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestComponent;
import org.smoothbuild.evaluator.ScheduleEvaluate;
import org.smoothbuild.evaluator.ScheduleEvaluate.EvaluateCore;
import org.smoothbuild.virtualmachine.dagger.VmTestModule;

@Component(
    modules = {
      VmTestModule.class,
      ReportTestModule.class,
      InitializerModule.class,
    })
@PerCommand
public interface EvaluatorTestComponent extends FrontendCompilerTestComponent {
  RunnableScheduler runnableScheduler();

  ScheduleEvaluate scheduleEvaluate();

  EvaluateCore evaluateCore();

  @Component.Builder
  public interface Builder {
    @BindsInstance
    Builder runnableScheduler(RunnableScheduler runnableScheduler);

    @BindsInstance
    Builder fileSystem(FileSystem<FullPath> fileSystem);

    EvaluatorTestComponent build();
  }
}

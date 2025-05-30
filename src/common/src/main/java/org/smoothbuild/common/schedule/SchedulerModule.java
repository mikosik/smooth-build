package org.smoothbuild.common.schedule;

import dagger.Binds;
import dagger.Module;

@Module
public interface SchedulerModule {
  @Binds
  RunnableScheduler runnableScheduler(VirtualThreadRunnableScheduler scheduler);
}

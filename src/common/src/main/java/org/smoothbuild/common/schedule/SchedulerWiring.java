package org.smoothbuild.common.schedule;

import com.google.inject.AbstractModule;

public class SchedulerWiring extends AbstractModule {
  @Override
  protected void configure() {
    bind(RunnableScheduler.class).to(VirtualThreadRunnableScheduler.class);
  }
}

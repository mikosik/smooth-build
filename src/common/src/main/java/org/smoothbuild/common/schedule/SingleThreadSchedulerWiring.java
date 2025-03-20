package org.smoothbuild.common.schedule;

import com.google.inject.AbstractModule;

public class SingleThreadSchedulerWiring extends AbstractModule {
  @Override
  protected void configure() {
    bind(RunnableScheduler.class).to(SingleThreadRunnableScheduler.class);
  }
}

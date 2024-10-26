package org.smoothbuild.common.task;

import com.google.inject.AbstractModule;
import com.google.inject.Key;

public class SchedulerWiring extends AbstractModule {
  @Override
  protected void configure() {
    bind(Key.get(Integer.class, ThreadCount.class))
        .toInstance(Runtime.getRuntime().availableProcessors());
  }
}

package org.smoothbuild.testing.accept;

import static org.smoothbuild.run.eval.report.TaskMatchers.ALL;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.eval.report.TaskMatcher;
import org.smoothbuild.virtualmachine.evaluate.Sandbox;

public class TestModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(MemoryReporter.class).toInstance(new MemoryReporter());
    bind(Reporter.class).to(MemoryReporter.class);
    bind(TaskMatcher.class).toInstance(ALL);
  }

  @Provides
  @Singleton
  @Sandbox
  public Hash provideSandboxHash() {
    return Hash.of(33);
  }
}

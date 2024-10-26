package org.smoothbuild.backendcompile.testing;

import com.google.inject.AbstractModule;
import org.smoothbuild.common.task.SchedulerWiring;
import org.smoothbuild.compilerbackend.CompilerBackendWiring;
import org.smoothbuild.virtualmachine.testing.TestingVmWiring;

// TODO is this wiring similar to EvaluatorTestCase.TestWiring?
public class TestingBackendCompileWiring extends AbstractModule {
  @Override
  protected void configure() {
    install(new CompilerBackendWiring());
    install(new TestingVmWiring());
    install(new SchedulerWiring());
  }
}

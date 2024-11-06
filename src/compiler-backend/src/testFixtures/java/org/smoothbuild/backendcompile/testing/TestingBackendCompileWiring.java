package org.smoothbuild.backendcompile.testing;

import com.google.inject.AbstractModule;
import org.smoothbuild.common.testing.CommonTestWiring;
import org.smoothbuild.compilerbackend.CompilerBackendWiring;
import org.smoothbuild.virtualmachine.testing.VmTestWiring;

// TODO is this wiring similar to EvaluatorTestCase.TestWiring?
public class TestingBackendCompileWiring extends AbstractModule {
  @Override
  protected void configure() {
    install(new CompilerBackendWiring());
    install(new VmTestWiring());
    install(new CommonTestWiring());
  }
}

package org.smoothbuild.compilerfrontend.dagger;

import static org.smoothbuild.common.testing.TestingInitializer.runInitializer;

public class FrontendCompilerTestContext implements FrontendCompilerTestApi {
  private final FrontendCompilerTestComponent component;

  public FrontendCompilerTestContext() {
    this.component = DaggerFrontendCompilerTestComponent.create();
    runInitializer(component);
  }

  @Override
  public FrontendCompilerTestComponent provide() {
    return component;
  }
}

package org.smoothbuild.cli.dagger;

import static org.smoothbuild.common.testing.TestingInitializer.runInitializer;

public class CliTestContext implements CliTestApi {
  private final CliTestComponent component;

  public CliTestContext() {
    this.component = DaggerCliTestComponent.create();
    runInitializer(component);
  }

  @Override
  public CliTestComponent provide() {
    return component;
  }
}

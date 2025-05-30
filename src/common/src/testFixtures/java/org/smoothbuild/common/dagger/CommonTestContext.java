package org.smoothbuild.common.dagger;

public class CommonTestContext implements CommonTestApi {
  private final CommonTestComponent component;

  public CommonTestContext() {
    this.component = DaggerCommonTestComponent.create();
  }

  public CommonTestComponent provide() {
    return component;
  }
}

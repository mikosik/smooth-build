package org.smoothbuild.acceptance.cmd;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public abstract class DefaultModuleTestCase extends AcceptanceTestCase {
  @Test
  public void missing_default_module_causes_error() {
    whenSmooth(commandNameWithArgument());
    thenFinishedWithError();
    thenSysOutContains("error: 'build.smooth' doesn't exist.\n");
  }

  protected abstract String[] commandNameWithArgument();
}

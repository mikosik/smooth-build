package org.smoothbuild.acceptance.cli.command.common;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public abstract class DefaultModuleTestCase extends AcceptanceTestCase {
  @Test
  public void missing_default_module_causes_error() {
    whenSmooth(commandNameWithArgument());
    thenFinishedWithError();
    thenSysOutContainsParseError("'build.smooth' doesn't exist.");
  }

  protected abstract String[] commandNameWithArgument();
}

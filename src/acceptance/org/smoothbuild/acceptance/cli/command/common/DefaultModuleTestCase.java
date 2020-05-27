package org.smoothbuild.acceptance.cli.command.common;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.CommandWithArgs;

public abstract class DefaultModuleTestCase extends AcceptanceTestCase {
  @Test
  public void missing_default_module_causes_error_without_creating_smooth_dir() {
    whenSmooth(commandNameWithArgument());
    thenFinishedWithError();
    thenSysOutContains("smooth: error: Directory '.' doesn't have 'build.smooth'. " +
        "Is it really smooth enabled project?");
    assertThat(smoothDir().exists())
        .isFalse();
  }

  protected abstract CommandWithArgs commandNameWithArgument();
}

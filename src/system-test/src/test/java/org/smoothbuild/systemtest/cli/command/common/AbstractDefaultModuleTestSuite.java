package org.smoothbuild.systemtest.cli.command.common;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestContext;

public abstract class AbstractDefaultModuleTestSuite extends SystemTestContext {
  @Test
  void missing_default_module_causes_error_without_creating_smooth_dir() {
    runSmooth(commandNameWithArg());
    assertFinishedWithError();
    assertSystemOutContains("smooth: error: Current directory doesn't have 'build.smooth'. "
        + "Is it really smooth enabled project?");
    assertThat(Files.exists(smoothDirAbsolutePath())).isFalse();
  }

  protected abstract CommandWithArgs commandNameWithArg();
}

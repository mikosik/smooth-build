package org.smoothbuild.acceptance.cmd;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CleanCommandTest extends AcceptanceTestCase {
  @Test
  public void clean_command_deletes_smooth_dir() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    whenSmoothClean();
    thenFinishedWithSuccess();
    assertThat(new File(projectDir(), SMOOTH_DIR.toString()).exists())
        .isFalse();
  }

  @Test
  public void clean_command_with_arguments_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothClean("some arguments");
    thenFinishedWithError();
    thenSysErrContains(
        "Unmatched arguments from index 1: 'some', 'arguments'",
        "",
        "Usage:",
        "smooth clean",
        "Try 'smooth help clean' for more information.",
        "");
  }
}

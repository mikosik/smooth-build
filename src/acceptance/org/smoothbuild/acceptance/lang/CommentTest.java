package org.smoothbuild.acceptance.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CommentTest extends AcceptanceTestCase {
  @Test
  public void full_line_comment() throws IOException {
    createUserModule(
        "  # ((( full line comment '  ",
        "  result = '';               ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("");
  }

  @Test
  public void trailing_comment() throws IOException {
    createUserModule(
        "  result = '' ;  # comment at the end of line  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("");
  }
}

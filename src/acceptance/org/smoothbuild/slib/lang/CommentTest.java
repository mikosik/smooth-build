package org.smoothbuild.slib.lang;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.slib.AcceptanceTestCase;

public class CommentTest extends AcceptanceTestCase {
  @Test
  public void full_line_comment() throws IOException {
    givenScript(
        "  # ((( full line comment '  ",
        "  result = '';               ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("");
  }

  @Test
  public void trailing_comment() throws IOException {
    givenScript(
        "  result = '' ;  # comment at the end of line  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("");
  }
}

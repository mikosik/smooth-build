package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CommentTest extends AcceptanceTestCase {
  @Test
  public void full_line_comment() throws IOException {
    givenScript(
        "  # ((( full line comment '  ",
        "  result = '';               ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent(""));
  }

  @Test
  public void trailing_comment() throws IOException {
    givenScript(
        "  result = '' ;  # comment at the end of line  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent(""));
  }
}

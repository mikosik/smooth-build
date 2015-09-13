package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CommentTest extends AcceptanceTestCase {

  @Test
  public void full_line_comment() throws IOException {
    givenBuildScript(script("# ((( full line comment '\n result : '';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent(""));
  }

  @Test
  public void trailing_comment() throws IOException {
    givenBuildScript(script("result : '' ;  # comment at the end of line"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent(""));
  }
}

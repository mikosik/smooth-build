package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class CommentSmoothTest extends IntegrationTestCase {
  @Test
  public void comments_are_ignored() throws IOException {
    // given
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("# full line comment");
    builder.addLine("run: newFile(path='file.txt', 'content'); # comment at the end of line");
    script(builder.build());

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
  }

  @Test
  public void comment_char_is_allowed_in_strings() throws IOException {
    // given
    script("run: newFile(path='file.txt', '###');");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
  }
}

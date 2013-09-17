package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.problem.CodeLocation.codeLocation;
import static org.smoothbuild.problem.MessageType.WARNING;

import org.junit.Test;

public class CodeMessageTest {

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbidden() throws Exception {
    new CodeMessage(WARNING, null, "message");
  }

  @Test
  public void testToString() throws Exception {
    Message message = new CodeMessage(WARNING, codeLocation(1, 2, 3), "problem description");
    assertThat(message.toString()).isEqualTo("WARNING[1:2-3]: problem description");
  }
}

package org.smoothbuild.message.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.message.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class CodeMessageTest {

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbidden() throws Exception {
    new CodeMessage(WARNING, null, "message");
  }

  @Test
  public void testCodeLocation() throws Exception {
    CodeLocation codeLocation = new FakeCodeLocation();
    CodeMessage message = new CodeMessage(WARNING, codeLocation, "problem description");
    assertThat(message.codeLocation()).isEqualTo(codeLocation);
  }

  @Test
  public void testToString() throws Exception {
    Message message = new CodeMessage(WARNING, codeLocation(1, 2, 4), "problem description");
    assertThat(message.toString()).isEqualTo("WARNING [2:3-4]: problem description");
  }
}

package org.smoothbuild.message.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.message.base.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.message.base.Message;
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
    CodeLocation codeLocation = codeLocation(2);
    Message message = new CodeMessage(WARNING, codeLocation, "problem description");
    assertThat(message.toString()).isEqualTo("WARNING " + codeLocation + ": problem description");
  }
}

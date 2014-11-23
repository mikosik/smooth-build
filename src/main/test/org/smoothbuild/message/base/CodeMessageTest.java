package org.smoothbuild.message.base;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.message.base.MessageType.WARNING;

import org.junit.Test;

public class CodeMessageTest {

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbidden() throws Exception {
    new CodeMessage(WARNING, null, "message");
  }

  @Test
  public void testCodeLocation() throws Exception {
    CodeLocation codeLocation = codeLocation(1);
    CodeMessage message = new CodeMessage(WARNING, codeLocation, "problem description");
    assertEquals(codeLocation, message.codeLocation());
  }

  @Test
  public void testToString() throws Exception {
    CodeLocation codeLocation = codeLocation(2);
    Message message = new CodeMessage(WARNING, codeLocation, "problem description");
    assertEquals("WARNING " + codeLocation + ": problem description", message.toString());
  }
}

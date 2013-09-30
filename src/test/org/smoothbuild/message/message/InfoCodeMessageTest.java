package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.INFO;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class InfoCodeMessageTest {
  CodeLocation codeLocation = codeLocation(1, 2, 4);
  String message = "message";
  InfoCodeMessage info;

  @Test
  public void type() {
    given(info = new InfoCodeMessage(codeLocation, message));
    when(info).type();
    thenReturned(INFO);
  }
}

package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.INFO;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class CodeInfoTest {
  CodeLocation codeLocation = codeLocation(1, 2, 4);
  String message = "message";
  CodeInfo info;

  @Test
  public void type() {
    given(info = new CodeInfo(codeLocation, message));
    when(info).type();
    thenReturned(INFO);
  }
}

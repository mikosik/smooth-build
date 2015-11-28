package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.MessageType.ERROR;
import static org.smoothbuild.lang.message.MessageType.INFO;
import static org.smoothbuild.lang.message.MessageType.WARNING;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class MessageTypeTest {

  @Test
  public void name_plural_of_error() {
    when(ERROR.namePlural());
    thenReturned("error(s)");
  }

  @Test
  public void name_plural_of_warning() {
    when(WARNING.namePlural());
    thenReturned("warning(s)");
  }

  @Test
  public void name_plural_of_info() {
    when(INFO.namePlural());
    thenReturned("info(s)");
  }
}

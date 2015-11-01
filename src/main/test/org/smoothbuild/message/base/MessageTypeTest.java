package org.smoothbuild.message.base;

import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.SUGGESTION;
import static org.smoothbuild.message.base.MessageType.WARNING;
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
  public void name_plural_of_suggestion() {
    when(SUGGESTION.namePlural());
    thenReturned("suggestion(s)");
  }

  @Test
  public void name_plural_of_info() {
    when(INFO.namePlural());
    thenReturned("info(s)");
  }
}

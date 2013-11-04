package org.smoothbuild.message.message;

import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.FATAL;
import static org.smoothbuild.message.message.MessageType.INFO;
import static org.smoothbuild.message.message.MessageType.SUGGESTION;
import static org.smoothbuild.message.message.MessageType.WARNING;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class MessageTypeTest {

  @Test
  public void name_plural_of_fatal() {
    when(FATAL.namePlural());
    thenReturned("fatals");
  }

  @Test
  public void name_plural_of_error() {
    when(ERROR.namePlural());
    thenReturned("errors");
  }

  @Test
  public void name_plural_of_warning() {
    when(WARNING.namePlural());
    thenReturned("warnings");
  }

  @Test
  public void name_plural_of_suggestion() {
    when(SUGGESTION.namePlural());
    thenReturned("suggestions");
  }

  @Test
  public void name_plural_of_info() {
    when(INFO.namePlural());
    thenReturned("infos");
  }
}

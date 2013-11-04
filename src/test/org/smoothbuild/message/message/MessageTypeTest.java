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

  // isProblem()

  @Test
  public void fatal_is_a_problem() {
    when(FATAL.isProblem());
    thenReturned(true);
  }

  @Test
  public void error_is_a_problem() {
    when(ERROR.isProblem());
    thenReturned(true);
  }

  @Test
  public void warning_is_not_a_problem() {
    when(WARNING.isProblem());
    thenReturned(false);
  }

  @Test
  public void suggestion_is_not_a_problem() {
    when(SUGGESTION.isProblem());
    thenReturned(false);
  }

  @Test
  public void info_is_not_a_problem() {
    when(INFO.isProblem());
    thenReturned(false);
  }

  // namePlural()

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

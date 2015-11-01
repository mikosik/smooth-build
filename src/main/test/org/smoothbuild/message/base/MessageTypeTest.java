package org.smoothbuild.message.base;

import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.SUGGESTION;
import static org.smoothbuild.message.base.MessageType.WARNING;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class MessageTypeTest {

  // isProblem()

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

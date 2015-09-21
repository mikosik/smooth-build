package org.smoothbuild.testing.message;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;

public class ContainsMessage extends TypeSafeMatcher<LoggedMessages> {
  private final Class<?> messageClass;

  public static Matcher<LoggedMessages> containsMessage(Class<?> messageClass) {
    return new ContainsMessage(messageClass);
  }

  private ContainsMessage(Class<?> messageClass) {
    this.messageClass = messageClass;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("contains only Message = '" + messageClass + "'");
  }

  @Override
  protected boolean matchesSafely(LoggedMessages item) {
    for (Message message : item) {
      if (message.getClass() == messageClass) {
        return true;
      }
    }
    return false;
  }
}

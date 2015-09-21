package org.smoothbuild.testing.message;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.smoothbuild.message.listen.LoggedMessages;

import com.google.common.collect.Iterables;

public class ContainsOnlyMessageMatcher extends TypeSafeMatcher<LoggedMessages> {
  private final Class<?> messageClass;

  public static Matcher<LoggedMessages> containsOnlyMessage(Class<?> messageClass) {
    return new ContainsOnlyMessageMatcher(messageClass);
  }

  private ContainsOnlyMessageMatcher(Class<?> messageClass) {
    this.messageClass = messageClass;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("contains only Message = '" + messageClass + "'");
  }

  @Override
  protected boolean matchesSafely(LoggedMessages item) {
    return Iterables.size(item) == 1 && item.iterator().next().getClass() == messageClass;
  }
}

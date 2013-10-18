package org.smoothbuild.testing.message;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.Message;

public class ErrorMessageMatchers {

  public static Matcher<ErrorMessageException> containsInstanceOf(
      final Class<? extends Message> expectedClass) {
    return new TypeSafeMatcher<ErrorMessageException>() {
      private Class<? extends Message> actual;

      @Override
      public void describeTo(Description description) {
        description.appendText(ErrorMessageException.class.getSimpleName());
        description.appendText(" containing ");
        description.appendText(expectedClass.getSimpleName());
      }

      @Override
      protected boolean matchesSafely(ErrorMessageException item) {
        actual = item.errorMessage().getClass();
        return expectedClass.isAssignableFrom(actual);
      }
    };
  }
}

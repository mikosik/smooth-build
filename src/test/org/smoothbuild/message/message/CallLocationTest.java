package org.smoothbuild.message.message;

import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.testory.common.Closure;

public class CallLocationTest {
  String name;
  CodeLocation codeLocation;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void nullNameIsForbidden() {
    when(callLocation(null, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void nullCallLocationIsForbidden() {
    when(callLocation(name, null));
    thenThrown(NullPointerException.class);
  }

  private static Closure callLocation(final String name, final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public CallLocation invoke() {
        return CallLocation.callLocation(name, codeLocation);
      }
    };
  }
}

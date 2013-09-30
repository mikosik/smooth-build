package org.smoothbuild.message.message;

import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.function.base.Name;
import org.testory.common.Closure;

public class TaskLocationTest {
  Name name;
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

  private static Closure callLocation(final Name name, final CodeLocation codeLocation) {
    return new Closure() {
      public TaskLocation invoke() {
        return TaskLocation.taskLocation(name, codeLocation);
      }
    };
  }
}

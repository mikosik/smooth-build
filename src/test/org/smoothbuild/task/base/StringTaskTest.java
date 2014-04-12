package org.smoothbuild.task.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class StringTaskTest {
  SString string = new FakeString("some string");
  CodeLocation codeLocation = new FakeCodeLocation();
  StringTask task = new StringTask(string, codeLocation);

  @Test(expected = NullPointerException.class)
  public void null_result_is_forbidden() throws Exception {
    new StringTask(null, codeLocation);
  }

  @Test
  public void execute_returns_string_passed_to_constructor() {
    given(task = new StringTask(string, codeLocation));
    when(task.execute(null));
    thenReturned(string);
  }
}

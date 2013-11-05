package org.smoothbuild.task.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.plugin.FakeString;

public class StringTaskTest {
  StringValue string = new FakeString("some string");
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

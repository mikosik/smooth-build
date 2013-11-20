package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.contains;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.Lists;

public class StringSetTaskTest {
  FakeSandbox sandbox = new FakeSandbox();
  CodeLocation codeLocation = new FakeCodeLocation();
  StringValue string1 = sandbox.objectDb().string("string1");
  StringValue string2 = sandbox.objectDb().string("string2");

  Result result1 = new FakeResult(string1);
  Result result2 = new FakeResult(string2);

  StringSetTask stringSetTask;

  @Test
  public void execute() {
    given(stringSetTask = new StringSetTask(Lists.newArrayList(result1, result2), codeLocation));
    when(stringSetTask.execute(sandbox));
    thenReturned(contains(string1, string2));
  }
}

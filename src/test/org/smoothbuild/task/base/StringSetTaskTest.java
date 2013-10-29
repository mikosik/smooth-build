package org.smoothbuild.task.base;

import static org.smoothbuild.testing.plugin.StringSetMatchers.containsOnly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.Lists;

public class StringSetTaskTest {
  FakeSandbox sandbox = new FakeSandbox();
  StringValue string1 = sandbox.objectDb().string("string1");
  StringValue string2 = sandbox.objectDb().string("string2");

  Result result1 = new FakeResult(string1);
  Result result2 = new FakeResult(string2);

  StringSetTask stringSetTask;

  @Test
  public void execute() {
    given(stringSetTask = new StringSetTask(Lists.newArrayList(result1, result2)));
    when(stringSetTask.execute(sandbox));
    thenReturned(containsOnly(string1.value(), string2.value()));
  }
}

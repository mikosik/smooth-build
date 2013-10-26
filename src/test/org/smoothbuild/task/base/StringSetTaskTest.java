package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.plugin.StringSetMatchers.containsOnly;

import org.junit.Test;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.testing.task.base.FakeTask;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.Lists;

public class StringSetTaskTest {
  FakeSandbox sandbox = new FakeSandbox();
  StringValue string1 = sandbox.objectDb().string("string1");
  StringValue string2 = sandbox.objectDb().string("string2");

  Task task1 = new FakeTask(string1);
  Task task2 = new FakeTask(string2);

  CodeLocation codeLocation = codeLocation(1, 2, 4);

  StringSetTask stringSetTask = new StringSetTask(Lists.newArrayList(task1, task2), codeLocation);

  @Test
  public void dependencies() {
    assertThat(stringSetTask.dependencies()).containsOnly(task1, task2);
  }

  @Test
  public void execute() {
    stringSetTask.execute(sandbox);
    assertThat((StringSet) stringSetTask.result(), containsOnly(string1.value(), string2.value()));
  }
}

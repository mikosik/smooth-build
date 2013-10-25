package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.plugin.StringSetMatchers.containsOnly;
import static org.smoothbuild.testing.task.base.TaskTester.hashes;
import static org.smoothbuild.testing.task.exec.HashedTasksTester.hashedTasks;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.testing.task.base.FakeTask;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.hash.HashCode;

public class StringSetTaskTest {
  FakeSandbox sandbox = new FakeSandbox();
  StringValue string1 = sandbox.objectDb().string("string1");
  StringValue string2 = sandbox.objectDb().string("string2");

  Task task1 = new FakeTask(string1);
  Task task2 = new FakeTask(string2);

  CodeLocation codeLocation = codeLocation(1, 2, 4);
  List<HashCode> hashes = hashes(task1, task2);

  StringSetTask stringSetTask = new StringSetTask(hashes, codeLocation);

  @Test
  public void dependencies() {
    assertThat(stringSetTask.dependencies()).containsOnly(task1.hash(), task2.hash());
  }

  @Test
  public void execute() {
    stringSetTask.execute(sandbox, hashedTasks(task1, task2));
    assertThat((StringSet) stringSetTask.result(), containsOnly(string1.value(), string2.value()));
  }

  @Test
  public void hashOfEmptyStringSetIsDifferentFromHashOfStringSetWithOneElement() throws Exception {
    StringSetTask stringSetTask1 = new StringSetTask(hashes(task1), codeLocation);
    StringSetTask stringSetTask2 = new StringSetTask(hashes(), codeLocation);

    assertThat(stringSetTask1.hash()).isNotEqualTo(stringSetTask2.hash());
  }

  @Test
  public void hashOfStringSetWithOneStringIsDifferentFromHashOfStringSetWithDifferentElement()
      throws Exception {
    StringSetTask stringSetTask1 = new StringSetTask(hashes(task1), codeLocation);
    StringSetTask stringSetTask2 = new StringSetTask(hashes(task2), codeLocation);

    assertThat(stringSetTask1.hash()).isNotEqualTo(stringSetTask2.hash());
  }

  @Test
  public void hashOfStringSetWithOneStringIsDifferentFromHashOfStringSetWithAdditionalString()
      throws Exception {
    StringSetTask stringSetTask1 = new StringSetTask(hashes(task1), codeLocation);
    StringSetTask stringSetTask2 = new StringSetTask(hashes(task1, task2), codeLocation);

    assertThat(stringSetTask1.hash()).isNotEqualTo(stringSetTask2.hash());
  }
}

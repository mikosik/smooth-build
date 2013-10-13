package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.task.HashedTasksTester.hashedTasks;
import static org.smoothbuild.testing.task.TaskTester.hashes;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.StringSetTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.task.TestTask;
import org.smoothbuild.type.api.StringSet;

import com.google.common.hash.HashCode;

public class StringSetTaskTest {
  String string1 = "string1";
  String string2 = "string2";

  Task task1 = new TestTask(string1);
  Task task2 = new TestTask(string2);

  CodeLocation codeLocation = codeLocation(1, 2, 4);
  List<HashCode> hashes = hashes(task1, task2);

  StringSetTask stringSetTask = new StringSetTask(hashes, codeLocation);

  @Test
  public void dependencies() {
    assertThat(stringSetTask.dependencies()).containsOnly(task1.hash(), task2.hash());
  }

  @Test
  public void execute() {
    stringSetTask.execute(null, hashedTasks(task1, task2));
    assertThat((StringSet) stringSetTask.result()).containsOnly(string1, string2);
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

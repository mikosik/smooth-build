package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.task.HashedTasksTester.hashedTasks;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.testing.task.TestTask;
import org.smoothbuild.type.api.StringSet;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class StringSetTaskTest {
  String string1 = "string1";
  String string2 = "string2";
  Task task1 = new TestTask(string1);
  Task task2 = new TestTask(string2);
  CodeLocation codeLocation = codeLocation(1, 2, 4);
  private final List<HashCode> hashes = ImmutableList.of(task1.hash(), task2.hash());

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
}

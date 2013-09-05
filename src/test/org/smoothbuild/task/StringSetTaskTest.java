package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.plugin.StringSet;

import com.google.common.collect.ImmutableSet;

public class StringSetTaskTest {
  String string1 = "string1";
  String string2 = "string2";
  Task task1 = new PrecalculatedTask(string1);
  Task task2 = new PrecalculatedTask(string2);

  StringSetTask stringSetTask = new StringSetTask(ImmutableSet.of(task1, task2));

  @Test
  public void dependencies() {
    assertThat(stringSetTask.dependencies()).containsOnly(task1, task2);
  }

  @Test
  public void execute() {
    stringSetTask.execute(null);
    assertThat((StringSet) stringSetTask.result()).containsOnly(string1, string2);
  }

}

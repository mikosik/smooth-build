package org.smoothbuild.common.task;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.task.Tasks.task1;
import static org.smoothbuild.common.task.Tasks.task2;

import org.junit.jupiter.api.Test;

public class TasksTest {
  @Test
  void task1_invokes_function() {
    var label = label("label");
    var mapTask = task1(label, (String s) -> s + "!");
    assertThat(mapTask.execute("abc")).isEqualTo(output("abc!", label, list()));
  }

  @Test
  void task2_invokes_function() {
    var label = label("label");
    var mapTask = task2(label, (String s1, String s2) -> s1 + s2);
    assertThat(mapTask.execute("abc", "def")).isEqualTo(output("abcdef", label, list()));
  }
}

package org.smoothbuild.common.schedule;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.common.schedule.Tasks.task1;
import static org.smoothbuild.common.schedule.Tasks.task2;
import static org.smoothbuild.common.schedule.Tasks.taskX;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;

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

  @Test
  void taskX_invokes_function() {
    var label = label("label");
    var mapTask = taskX(label, (List<String> list) -> list.toString(","));
    assertThat(mapTask.execute(list("a", "b", "c"))).isEqualTo(output("a,b,c", label, list()));
  }
}

package org.smoothbuild.common.task;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.task.Output.output;

import org.junit.jupiter.api.Test;

public class TasksTest {
  @Test
  void map_maps() {
    var label = label("label");
    var mapTask = Tasks.map(label, (String s) -> s + "!");
    assertThat(mapTask.execute("abc")).isEqualTo(output("abc!", label, list()));
  }
}

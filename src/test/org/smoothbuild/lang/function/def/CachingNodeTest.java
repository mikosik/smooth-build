package org.smoothbuild.lang.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class CachingNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  Node node = node();
  Task task = mock(Task.class);

  CachingNode cachingNode = new CachingNode(node);

  @Test
  public void first_generate_task_call_forwards_call_to_node_generate_task() {
    given(willReturn(task), node).generateTask(taskGenerator);
    assertThat(cachingNode.generateTask(taskGenerator)).isEqualTo(task);
  }

  @Test
  public void second_generate_task_call_uses_cached_value() {
    given(willReturn(task), node).generateTask(taskGenerator);

    cachingNode.generateTask(taskGenerator);
    assertThat(cachingNode.generateTask(taskGenerator)).isEqualTo(task);

    thenCalledTimes(1,node).generateTask(taskGenerator);
  }

  private static Node node() {
    Node node = mock(Node.class);
    given(willReturn(new FakeCodeLocation()), node).codeLocation();
    given(willReturn(STRING), node).type();
    return node;
  }
}

package org.smoothbuild.lang.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.lang.type.STypes.STRING;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
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
    Mockito.when(node.generateTask(taskGenerator)).thenReturn(task);
    assertThat(cachingNode.generateTask(taskGenerator)).isEqualTo(task);
  }

  @Test
  public void second_generate_task_call_uses_cached_value() {
    Mockito.when(node.generateTask(taskGenerator)).thenReturn(task);

    cachingNode.generateTask(taskGenerator);
    assertThat(cachingNode.generateTask(taskGenerator)).isEqualTo(task);

    verify(node, times(1)).generateTask(taskGenerator);
  }

  private static Node node() {
    Node node = mock(Node.class);
    Mockito.when(node.codeLocation()).thenReturn(new FakeCodeLocation());
    BDDMockito.willReturn(STRING).given(node).type();
    return node;
  }
}

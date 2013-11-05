package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.exec.TaskGenerator;

public class CachingNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  LocatedNode node = mock(LocatedNode.class);
  LocatedTask locatedTask = mock(LocatedTask.class);

  CachingNode cachingNode = new CachingNode(node);

  @Test
  public void first_generate_task_call_forwards_call_to_node_generate_task() {
    Mockito.when(node.generateTask(taskGenerator)).thenReturn(locatedTask);
    assertThat(cachingNode.generateTask(taskGenerator)).isEqualTo(locatedTask);
  }

  @Test
  public void second_generate_task_call_uses_cached_value() {
    Mockito.when(node.generateTask(taskGenerator)).thenReturn(locatedTask);

    cachingNode.generateTask(taskGenerator);
    assertThat(cachingNode.generateTask(taskGenerator)).isEqualTo(locatedTask);

    verify(node, times(1)).generateTask(taskGenerator);
  }
}

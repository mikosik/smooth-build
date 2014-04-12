package org.smoothbuild.lang.function.def;

import static org.smoothbuild.lang.base.STypes.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class CachingNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  Node<?> node = node();
  Task<?> task = mock(Task.class);

  CachingNode<?> cachingNode = new CachingNode<>(node);

  @Test
  public void first_generate_task_call_forwards_call_to_node_generate_task() {
    given(willReturn(task), node).generateTask(taskGenerator);
    when(cachingNode).generateTask(taskGenerator);
    thenReturned(task);
  }

  @Test
  public void second_generate_task_call_uses_cached_value() {
    given(willReturn(task), node).generateTask(taskGenerator);
    given(cachingNode.generateTask(taskGenerator));
    when(cachingNode).generateTask(taskGenerator);
    thenReturned(task);
    thenCalledTimes(1, node).generateTask(taskGenerator);
  }

  private static Node<?> node() {
    Node<?> node = mock(Node.class);
    given(willReturn(new FakeCodeLocation()), node).codeLocation();
    given(willReturn(STRING), node).type();
    return node;
  }
}

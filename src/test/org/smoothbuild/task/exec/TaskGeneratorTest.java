package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.task.base.FakeTask;

import com.google.common.hash.HashCode;

public class TaskGeneratorTest {
  DefinitionNode node = mock(DefinitionNode.class);
  Task task = new FakeTask("one");
  HashedTasks hashedTasks = mock(HashedTasks.class);

  TaskGenerator taskGenerator = new TaskGenerator(hashedTasks);

  @Test
  public void generateTask_returns_hash_of_task_generated_by_node() {
    when(node.generateTask(taskGenerator)).thenReturn(task);
    HashCode hash = taskGenerator.generateTask(node);
    assertThat(hash).isEqualTo(task.hash());
  }

  @Test
  public void generated_task_is_added_to_HashedTasks() throws Exception {
    when(node.generateTask(taskGenerator)).thenReturn(task);
    taskGenerator.generateTask(node);
    verify(hashedTasks).add(task);
  }
}

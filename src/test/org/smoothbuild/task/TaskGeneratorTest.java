package org.smoothbuild.task;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.task.TestTask;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class TaskGeneratorTest {
  DefinitionNode node = mock(DefinitionNode.class);
  DefinitionNode node2 = mock(DefinitionNode.class);

  Task task1 = task("one");
  Task task2 = task("two");

  TaskGenerator taskGenerator = new TaskGenerator();
  HashCode hash;

  @Test
  public void generateTask_returns_hash_of_task_generated_by_node() {
    Mockito.when(node.generateTask(taskGenerator)).thenReturn(task1);

    when(taskGenerator.generateTask(node));
    thenReturned(task1.hash());
  }

  @Test
  public void first_task_with_given_hash_is_rembered() {
    given(task1 = task("one"));
    given(task2 = task("one"));
    Mockito.when(node.generateTask(taskGenerator)).thenReturn(task1);
    Mockito.when(node2.generateTask(taskGenerator)).thenReturn(task2);
    given(taskGenerator.generateTask(node));
    given(taskGenerator.generateTask(node2));

    when(taskGenerator.allTasks().get(task1.hash()));

    thenReturned(task1);
  }

  @Test
  public void allTasks_returns_generated_tasks() {
    Mockito.when(node.generateTask(taskGenerator)).thenReturn(task1);

    given(taskGenerator.generateTask(node));
    when(taskGenerator.allTasks());
    thenReturned(ImmutableMap.of(task1.hash(), task1));
  }

  private static Task task(String name) {
    return new TestTask(name);
  }
}

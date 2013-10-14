package org.smoothbuild.task.exec;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.task.base.TestTask;

public class HashedTasksTest {
  Task task1 = new TestTask("abc");
  Task task2 = new TestTask("cde");

  HashedTasks hashedTasks = new HashedTasks();

  @Test
  public void retrieving_task_with_given_hash() {
    given(hashedTasks).add(task1);
    when(hashedTasks.get(task1.hash()));
    thenReturned(task1);
  }

  @Test
  public void adding_two_tasks_with_the_same_hash_ends_with_the_first_being_added()
      throws Exception {
    task2 = Mockito.mock(Task.class);
    Mockito.when(task2.hash()).thenReturn(task1.hash());

    given(hashedTasks).add(task1);
    given(hashedTasks).add(task2);

    when(hashedTasks).get(task1.hash());
    thenReturned(task1);
  }

  @Test
  public void retrieving_not_added_task_causes_exception() {
    given(hashedTasks).add(task1);
    when(hashedTasks).get(task2.hash());
    thenThrown(NoTaskWithGivenHashException.class);
  }
}

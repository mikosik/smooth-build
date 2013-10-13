package org.smoothbuild.task;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.task.TestTask;

import com.google.common.collect.ImmutableMap;

public class HashedTasksTest {
  Task task1 = new TestTask("abc");
  Task task2 = new TestTask("cde");

  HashedTasks hashedTasks;

  @Test
  public void retrieving_task_with_given_hash() {
    given(hashedTasks = new HashedTasks(ImmutableMap.of(task1.hash(), task1)));
    when(hashedTasks.get(task1.hash()));
    thenReturned(task1);
  }

  @Test
  public void passing_hash_of_task_that_is_not_in_map_throws_exception() {
    given(hashedTasks = new HashedTasks(ImmutableMap.of(task1.hash(), task1)));
    when(hashedTasks).get(task2.hash());
    thenThrown(NoTaskWithGivenHashException.class);
  }
}

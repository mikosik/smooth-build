package org.smoothbuild.testing.task;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.HashedTasks;
import org.testory.common.Closure;

public class HashedTasksTesterTest {
  TestTask task1 = new TestTask("abc");
  TestTask task2 = new TestTask("cde");

  private HashedTasks tasks;

  @Test
  public void hashed_task_can_be_retrieved_from_HashedTasks_via_hash() {
    given(tasks = HashedTasksTester.hashedTasks(task1, task2));
    when(tasks.get(task1.hash()));
    thenReturned(task1);
  }

  @Test
  public void creating_HashedTasks_with_two_tasks_with_the_same_hash_fails() throws Exception {
    given(task1 = new TestTask("abc"));
    given(task2 = new TestTask("abc"));
    when($hashedTasks(task1, task2));
    thenThrown(IllegalArgumentException.class);
  }

  private static Closure $hashedTasks(final Task... tasks) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return HashedTasksTester.hashedTasks(tasks);
      }
    };
  }
}

package org.smoothbuild.testing.task.base;

import static org.hamcrest.Matchers.equalTo;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;

public class TaskTesterTest {
  Task task1 = new FakeTask("task1");
  Task task2 = new FakeTask("task2");

  @Test
  public void test() {
    when(TaskTester.hashes(task1, task2));
    thenReturned(equalTo(ImmutableList.of(task1.hash(), task2.hash())));
  }
}

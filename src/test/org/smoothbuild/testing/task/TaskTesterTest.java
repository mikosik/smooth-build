package org.smoothbuild.testing.task;

import static org.hamcrest.Matchers.equalTo;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class TaskTesterTest {
  Task task1 = new TestTask("task1");
  Task task2 = new TestTask("task2");
  ImmutableList<HashCode> hashes;

  @Test
  public void test() {
    given(hashes = TaskTester.hashes(task1, task2));
    when(hashes);
    thenReturned(equalTo(ImmutableList.of(task1.hash(), task2.hash())));
  }
}

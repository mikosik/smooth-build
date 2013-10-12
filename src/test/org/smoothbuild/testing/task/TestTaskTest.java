package org.smoothbuild.testing.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.plugin.api.Sandbox;

public class TestTaskTest {
  TestTask task = new TestTask("result");

  @Test
  public void initiallyResultIsCalculated() {
    assertThat(task.isResultCalculated()).isTrue();
  }

  @Test(expected = NullPointerException.class)
  public void nullResultIsForbidden() throws Exception {
    new TestTask(null);
  }

  @Test
  public void resultPassedToConstructorIsReturned() {
    String result = "result";
    TestTask task = new TestTask(result);
    assertThat(task.result()).isSameAs(result);
  }

  @Test
  public void hasZeroDependencies() throws Exception {
    assertThat(task.dependencies()).isEmpty();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void calculateResultThrowsException() throws Exception {
    task.execute(mock(Sandbox.class));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void locationThrowsException() throws Exception {
    task.location();
  }

  @Test
  public void hashOfDifferentObjectsAreDifferent() throws Exception {
    TestTask task1 = new TestTask("abc");
    TestTask task2 = new TestTask("def");
    assertThat(task1.hash()).isNotEqualTo(task2.hash());
  }

  @Test
  public void hashOfTasksWithTheSameWrappedObjectAreTheSame() throws Exception {
    String string = "abc";
    TestTask task1 = new TestTask(string);
    TestTask task2 = new TestTask(string);
    assertThat(task1.hash()).isEqualTo(task2.hash());
  }
}

package org.smoothbuild.testing.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.testing.task.base.FakeTask;
import org.smoothbuild.testing.task.exec.HashedTasksTester;

public class FakeTaskTest {
  FakeTask task = new FakeTask("result");

  @Test
  public void initiallyResultIsCalculated() {
    assertThat(task.isResultCalculated()).isTrue();
  }

  @Test(expected = NullPointerException.class)
  public void nullResultIsForbidden() throws Exception {
    new FakeTask(null);
  }

  @Test
  public void resultPassedToConstructorIsReturned() {
    String result = "result";
    FakeTask task = new FakeTask(result);
    assertThat(task.result()).isSameAs(result);
  }

  @Test
  public void hasZeroDependencies() throws Exception {
    assertThat(task.dependencies()).isEmpty();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void calculateResultThrowsException() throws Exception {
    task.execute(mock(Sandbox.class), HashedTasksTester.hashedTasks());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void locationThrowsException() throws Exception {
    task.location();
  }

  @Test
  public void hashOfDifferentObjectsAreDifferent() throws Exception {
    FakeTask task1 = new FakeTask("abc");
    FakeTask task2 = new FakeTask("def");
    assertThat(task1.hash()).isNotEqualTo(task2.hash());
  }

  @Test
  public void hashOfTasksWithTheSameWrappedObjectAreTheSame() throws Exception {
    String string = "abc";
    FakeTask task1 = new FakeTask(string);
    FakeTask task2 = new FakeTask(string);
    assertThat(task1.hash()).isEqualTo(task2.hash());
  }
}

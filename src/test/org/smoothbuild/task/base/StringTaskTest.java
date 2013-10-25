package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.exec.HashedTasks;
import org.smoothbuild.testing.plugin.FakeString;

public class StringTaskTest {
  StringValue string = new FakeString("some string");
  StringTask task = new StringTask(string);

  @Test
  public void initiallyResultIsCalculated() {
    assertThat(task.isResultCalculated()).isTrue();
  }

  @Test(expected = NullPointerException.class)
  public void nullResultIsForbidden() throws Exception {
    new StringTask(null);
  }

  @Test
  public void stringPassedToConstructorIsReturnedByResult() {
    StringTask task = new StringTask(string);
    assertThat(task.result()).isSameAs(string);
  }

  @Test
  public void hasZeroDependencies() throws Exception {
    assertThat(task.dependencies()).isEmpty();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void calculateResultThrowsException() throws Exception {
    task.execute(mock(Sandbox.class), mock(HashedTasks.class));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void locationThrowsException() throws Exception {
    task.location();
  }

  @Test
  public void hashesOfStringTasksWithDifferentStringsDiffer() throws Exception {
    StringTask stringTask1 = new StringTask(new FakeString("abc"));
    StringTask stringTask2 = new StringTask(new FakeString("def"));

    assertThat(stringTask1.hash()).isNotEqualTo(stringTask2.hash());
  }
}

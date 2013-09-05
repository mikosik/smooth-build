package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.plugin.api.Sandbox;

public class PrecalculatedTaskTest {
  PrecalculatedTask task = new PrecalculatedTask("result");

  @Test
  public void initiallyResultIsCalculated() {
    assertThat(task.isResultCalculated()).isTrue();
  }

  @Test(expected = NullPointerException.class)
  public void nullResultIsForbidden() throws Exception {
    new PrecalculatedTask(null);
  }

  @Test
  public void resultPassedToConstructorIsReturned() {
    String result = "result";
    PrecalculatedTask task = new PrecalculatedTask(result);
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
}

package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PrecalculatedTaskTest {

  @Test
  public void initiallyResultIsCalculated() {
    PrecalculatedTask task = new PrecalculatedTask("result");
    assertThat(task.isResultCalculated()).isTrue();
  }

  @Test
  public void resultPassedToConstructorIsReturned() {
    String result = "result";
    PrecalculatedTask task = new PrecalculatedTask(result);
    assertThat(task.result()).isSameAs(result);
  }

}
